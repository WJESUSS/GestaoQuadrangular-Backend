package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String fotoPerfil,
        String email,
        Perfil perfil,
        boolean ativo,
        Long celulaId,
        String nomeCelula,
        String telefoneWhatsapp
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getFotoPerfil(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.isAtivo(),
                usuario.getCelula() != null ? usuario.getCelula().getId() : null,
                usuario.getCelula() != null ? usuario.getCelula().getNome() : "Sem Célula",
                usuario.getTelefoneWhatsapp()
        );
    }
}