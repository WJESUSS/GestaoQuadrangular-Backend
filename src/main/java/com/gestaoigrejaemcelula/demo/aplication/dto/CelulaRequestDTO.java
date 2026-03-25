package com.gestaoigrejaemcelula.demo.aplication.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CelulaRequestDTO(
        @NotBlank String nome,
        String anfitriao,
        String endereco,
        @NotBlank String bairro,
        @NotNull DayOfWeek diaSemana,
        @NotNull LocalTime horario,
        @NotNull(message = "O ID do líder é obrigatório") Long liderId
) {}
