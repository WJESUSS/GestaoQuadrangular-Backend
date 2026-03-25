package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;

public record UsuarioRequestDTO(
        String nome,
        String email,
        String senha,
        Perfil perfil,
        Long celulaId // <-- Adicione este campo
) {}