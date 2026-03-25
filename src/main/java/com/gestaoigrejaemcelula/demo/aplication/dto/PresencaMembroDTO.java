package com.gestaoigrejaemcelula.demo.aplication.dto;

// DTO Auxiliar para a lista de membros dentro do card
public record PresencaMembroDTO(
        Long id,
        String nomeMembro,
        boolean escolaBiblica,
        boolean cultoSemana,
        boolean domingoManha,
        boolean domingoNoite
) {}
