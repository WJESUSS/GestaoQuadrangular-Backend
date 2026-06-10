package com.gestaoigrejaemcelula.demo.aplication.dto;
import jakarta.validation.constraints.NotNull;

public record DiscipuladoRequestDTO(
        @NotNull Long membroId,
        @NotNull Long celulaId,
        boolean escolaBiblica,
        boolean quartaNoite,
        String nomeMembro,
        boolean quintaNoite,
        boolean domingoManha,
        boolean domingoNoite,
        String justEscolaBiblica,
        String justQuartaNoite,
        String justQuintaNoite,
        String justDomingoManha,
        String justDomingoNoite
) {}