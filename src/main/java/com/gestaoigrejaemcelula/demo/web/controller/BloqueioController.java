package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.service.BloqueioService;
import com.gestaoigrejaemcelula.demo.domain.entity.NumeroBloqueado;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Caminho consistente com o resto do webhook (/webhook/whatsapp/registros/...)
// que o painel já consome. Ajuste se o seu projeto usar outro prefixo.
@RestController
@RequestMapping("/webhook/whatsapp/registros/bloqueios")
@RequiredArgsConstructor
public class BloqueioController {

    private final BloqueioService bloqueioService;

    @GetMapping
    public Page<NumeroBloqueado> listar(@PageableDefault(size = 10) Pageable pageable) {
        return bloqueioService.listar(pageable);
    }

    @PostMapping
    public ResponseEntity<NumeroBloqueado> bloquear(@RequestBody Map<String, String> body) {
        String numero = body.get("numero");
        if (numero == null || numero.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        String motivo = body.get("motivo");
        return ResponseEntity.ok(bloqueioService.bloquear(numero, motivo));
    }

    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> desbloquear(@PathVariable String numero) {
        bloqueioService.desbloquear(numero);
        return ResponseEntity.noContent().build();
    }
}