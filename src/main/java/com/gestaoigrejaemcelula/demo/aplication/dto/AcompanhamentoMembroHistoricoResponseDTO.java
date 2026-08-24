package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcompanhamentoMembroHistoricoResponseDTO {

    private Long membroId;
    private String membroNome;

    private Long totalDiscipulados;
    private Integer totalPontos;
    private LocalDate ultimoDiscipulado;
    private LocalDate proximoPeriodoDisponivel;

    private List<AcompanhamentoIndividualResponseDTO> discipulados;
}
