package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.LoginDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.SolicitacaoCadastroLiderDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.SolicitacaoCadastroResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.TokenDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.AuthService;
import com.gestaoigrejaemcelula.demo.aplication.service.UsuarioService;
import com.gestaoigrejaemcelula.demo.security.config.LoginRateLimiter;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService  usuarioService;
    private final LoginRateLimiter   loginRateLimiter;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO dto,
                                   HttpServletRequest request) {

        String emailNormalizado = dto.email().trim().toLowerCase();

        // Pega o bucket deste email
        Bucket bucket = loginRateLimiter.getBucket(emailNormalizado);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            // Calcula segundos até poder tentar de novo
            long segundosParaRenovar = probe.getNanosToWaitForRefill() / 1_000_000_000;

            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                            "erro",      "Muitas tentativas de login.",
                            "mensagem",  "Tente novamente em " + formatarTempo(segundosParaRenovar) + ".",
                            "tentativas", 0
                    ));
        }

        try {
            LoginDTO novoDto = new LoginDTO(emailNormalizado, dto.senha());
            TokenDTO token = authService.login(novoDto);

            // Login bem-sucedido — reseta o contador
            loginRateLimiter.resetar(emailNormalizado);

            return ResponseEntity.ok(token);

        } catch (org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "erro",   "Conta suspensa.",
                            "mensagem", "Sua conta foi desativada por inatividade. Entre em contato com o administrador."
                    ));

        } catch (Exception e) {
            long tentativasRestantes = probe.getRemainingTokens();

            // Mensagem diferente quando está na última tentativa
            String aviso = tentativasRestantes == 0
                    ? "Conta bloqueada por 5 minutos."
                    : "Você tem " + tentativasRestantes + " tentativa(s) restante(s).";

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "erro",               "Credenciais inválidas.",
                            "mensagem",           aviso,
                            "tentativasRestantes", tentativasRestantes
                    ));
        }
    }

    private String formatarTempo(long segundos) {
        if (segundos < 60) return segundos + " segundos";
        long min = segundos / 60;
        long sec = segundos % 60;
        return sec > 0 ? min + " minuto(s) e " + sec + " segundo(s)" : min + " minuto(s)";
    }

    @PostMapping("/solicitar-cadastro-lider")
    public ResponseEntity<SolicitacaoCadastroResponseDTO> solicitarCadastroLider(
            @RequestBody @Valid SolicitacaoCadastroLiderDTO dto) {

        SolicitacaoCadastroResponseDTO resposta = usuarioService.solicitarCadastroLider(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}