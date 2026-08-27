package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CultoRelatorioPregadorDTO {
    private String pregador;
    private Long totalPregacoes;
    private Double mediaTotalGeral;
    private Integer maiorTotalGeral;
    private Integer menorTotalGeral;
}
