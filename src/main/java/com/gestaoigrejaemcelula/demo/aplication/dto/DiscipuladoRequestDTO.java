package com.gestaoigrejaemcelula.demo.aplication.dto;

public record DiscipuladoRequestDTO(
        Long membroId,
        Long celulaId,
        boolean escolaBiblica,
        boolean quartaNoite,
        boolean quintaNoite,
        boolean domingoManha,
        boolean domingoNoite
) {
}