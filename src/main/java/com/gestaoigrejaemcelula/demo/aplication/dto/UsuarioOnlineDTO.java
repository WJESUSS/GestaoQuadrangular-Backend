package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;

import java.time.Instant;

public record UsuarioOnlineDTO(
        Long id,
        String nome,
        String email,
        String perfil,
        String fotoPerfil,
        Instant ultimoHeartbeat
) {
    public UsuarioOnlineDTO(Usuario usuario, Instant ultimoHeartbeat) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name(),
                usuario.getFotoPerfil(),
                ultimoHeartbeat
        );
    }
}
