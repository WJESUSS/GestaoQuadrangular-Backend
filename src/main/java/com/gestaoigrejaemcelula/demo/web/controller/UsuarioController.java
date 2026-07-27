package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.aplication.service.UsuarioService;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.nio.file.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios") // ✅ corrigido
public class UsuarioController {

    private final UsuarioService service;
    private final UsuarioRepository repository;

    public UsuarioController(UsuarioService service, UsuarioRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping("/admin/dashboard")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Dashboard ADMIN acessado!");
    }

    // 1️⃣ Cadastrar usuário
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody @Valid CadastroUsuarioDTO dto) {
        Usuario usuario = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    // 2️⃣ Listar todos os usuários
    @GetMapping
    public ResponseEntity<List<UsuarioResumoDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // 3️⃣ Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = service.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    // 4️⃣ Atualizar usuário
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDTO dto) {
        // O Service já devolve o DTO pronto para o JSON
        UsuarioResponseDTO response = service.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    // 5️⃣ Deletar usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        service.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alternarStatus(@PathVariable Long id) {
        service.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/pendentes")
    public ResponseEntity<List<UsuarioResumoDTO>> listarPendentes() {
        return ResponseEntity.ok(service.listarPendentes());
    }
    @GetMapping("/com-alteracao-pendente")
    public ResponseEntity<List<UsuarioResumoDTO>> listarComAlteracaoPendente() {
        return ResponseEntity.ok(service.listarComAlteracaoPendente());
    }

    /**
     * Admin aprova a alteração de dados do líder — aplica e-mail/senha pendentes.
     * PATCH /usuarios/{id}/aprovar-alteracao
     */
    @PatchMapping("/{id}/aprovar-alteracao")
    public ResponseEntity<Void> aprovarAlteracao(@PathVariable Long id) {
        service.aprovarAlteracao(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Admin rejeita a alteração de dados do líder — descarta e-mail/senha pendentes.
     * PATCH /usuarios/{id}/rejeitar-alteracao
     */
    @PatchMapping("/{id}/rejeitar-alteracao")
    public ResponseEntity<Void> rejeitarAlteracao(@PathVariable Long id) {
        service.rejeitarAlteracao(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/solicitar-alteracao")
    public ResponseEntity<SolicitacaoAlteracaoResponseDTO> solicitarAlteracao(
            @RequestBody @Valid SolicitacaoAlteracaoDTO dto) {
        return ResponseEntity.ok(service.solicitarAlteracao(dto));
    }
    @GetMapping("/fotos")
    public ResponseEntity<Map<Long, String>> listarFotos() {
        return ResponseEntity.ok(service.listarFotos());
    }

    @PatchMapping("/{id}/foto")
    public ResponseEntity<Void> atualizarFoto(
            @PathVariable Long id,
            @RequestBody FotoPerfilDTO dto) throws AccessDeniedException {
        service.atualizarFoto(id, dto.getFotoBase64());
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getMe(Authentication auth) {
        Usuario u = repository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Não encontrado"));
        return ResponseEntity.ok(new UsuarioResponseDTO(u));
    }

}