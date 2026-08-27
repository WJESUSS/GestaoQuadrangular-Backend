package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CultoRelatorioCampanhaDTO {
    private String nomeCampanha;
    private Long totalCultos;
    private Long totalGeral;
    private Double mediaGeral;
}
