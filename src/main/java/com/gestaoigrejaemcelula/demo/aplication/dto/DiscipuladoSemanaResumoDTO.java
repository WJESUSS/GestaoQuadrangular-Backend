package com.gestaoigrejaemcelula.demo.aplication.dto;

import java.time.LocalDate;
import java.util.List;

public record DiscipuladoSemanaResumoDTO(
        LocalDate inicio,
        LocalDate fim,
        int totalMembros,
        int presencasTotais,
        double percentualPresenca,
        List<DiscipuladoResumoDTO> membros
) {}

