package com.gestaoigrejaemcelula.demo.aplication.dto;

import java.time.LocalDate;

public record DiscipuladoRelatorioResponseDTO(
        Long id,
        String nomeCelula,
        String nomeLider,
        String nomeMembro,
        LocalDate dataInicio,
        LocalDate dataFim,

        boolean cultoQuartaNoite,
        boolean cultoQuintaNoite,
        boolean domingoManha,
        boolean domingoNoite,


        String justEscolaBiblica,
        String justQuartaNoite,
        String justQuintaNoite,
        String justDomingoManha,
        String justDomingoNoite
) {
}