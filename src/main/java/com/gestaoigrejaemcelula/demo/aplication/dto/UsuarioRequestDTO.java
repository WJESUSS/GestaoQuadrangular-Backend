package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        String senha,
        Perfil perfil,
        Long celulaId,
        String telefoneWhatsapp
) {}
