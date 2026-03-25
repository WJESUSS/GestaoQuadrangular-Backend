package com.gestaoigrejaemcelula.demo.aplication.dto;

import java.time.LocalDate;

public record DiscipuladoRelatorioResponseDTO(
        Long id,
        String nomeCelula,    // <-- Aqui o nome vai limpo para o React
        String nomeLider,
        String nomeMembro,
        LocalDate dataInicio,
        LocalDate dataFim,
        boolean escolaBiblica,
        boolean cultoSemana,
        boolean domingoManha,
        boolean domingoNoite

) {

}