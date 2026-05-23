package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.SolicitacaoAlteracaoDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Map;

/**
 * Endpoints que o próprio usuário autenticado usa para gerenciar seu perfil.
 * Qualquer perfil autenticado pode acessar (líder, pastor, etc.).
 */
@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;

    /**
     * Líder solicita troca de e-mail e/ou senha.
     * Fica pendente até o admin aprovar.
     *
     * POST /perfil/solicitar-alteracao
     * Header: Authorization: Bearer <token>
     */
    @PatchMapping("/{id}/rejeitar-alteracao")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejeitarAlteracao(@PathVariable Long id) throws AccessDeniedException {
        usuarioService.rejeitarAlteracao(id);
        return ResponseEntity.noContent().build();
    }

}