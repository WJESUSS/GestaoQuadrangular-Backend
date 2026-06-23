package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;

public record UsuarioResumoDTO(
        Long id,
        String nome,
        String email,
        String perfil,
        boolean ativo,
        Long celulaId,
        String nomeCelula,
        String telefoneWhatsapp,
        String fotoPerfil
) {
    public UsuarioResumoDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name(),
                usuario.isAtivo(),
                usuario.getCelula() != null ? usuario.getCelula().getId() : null,
                usuario.getCelula() != null ? usuario.getCelula().getNome() : "Sem Célula",
                usuario.getTelefoneWhatsapp(),
                usuario.getFotoPerfil()
        );
    }
}
