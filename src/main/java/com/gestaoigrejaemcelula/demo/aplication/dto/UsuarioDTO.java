package com.gestaoigrejaemcelula.demo.aplication.dto;



import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;

public record UsuarioDTO(
        Long id,
        String nome,
        String email
      // opcional, se você tiver
) {
    public UsuarioDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
          // ou "Membro" se for nulo
        );
    }
}
