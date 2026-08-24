package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcompanhamentoMembroItemDTO {

    private Long membroId;
    private String membroNome;

    private Long totalDiscipuladosIndividuais;
    private Integer pontosIndividuais;
    private Long participacoesColetivas;
    private Integer pontosColetivos;
    private Integer totalPontos;

    private LocalDate ultimoDiscipulado;
    private Boolean discipuladoEstaSemana;
    private LocalDate proximoPeriodoDisponivel;

    private String statusSemanal;
    private String mensagemStatus;
}
