package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CultoRelatorioResumoDTO {
    private Long totalCultos;
    private Long totalMembros;
    private Long totalVisitantes;
    private Long totalCriancas;
    private Long totalDiaconos;
    private Long totalGeral;
    private Double mediaGeralPorCulto;
    private Double mediaPorTipoVitoria;
    private Double mediaPorTipoSantaCeia;
    private Double mediaPorTipoCelebracao;
    private Double mediaPorTipoMissoes;
}
