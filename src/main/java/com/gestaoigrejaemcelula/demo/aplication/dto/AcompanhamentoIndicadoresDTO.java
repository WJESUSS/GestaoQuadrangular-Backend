package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcompanhamentoIndicadoresDTO {

    private Integer pontosDiscipuladoSemana;
    private Integer pontosDiscipuladoMes;
    private Long discipuladosIndividuaisSemana;
    private Long discipuladosIndividuaisMes;
    private Long discipuladosColetivosMes;
    private Long discipuladosColetivosTotal;
    private Long participacoesColetivasTotal;
    private Long totalDiscipulados;
    private Integer totalPontos;

    private Long membrosDiscipulados;
    private Long membrosNaoDiscipuladosSemana;
    private List<String> nomesMembrosPendentesSemana;

    private Long totalMembrosAtivos;
}
