package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.*;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingCelulaDTO {
    private Long celulaId;
    private String nomeCelula;
    private String lider;
    private Integer presencaMedia;
    private Integer visitantes;
    private Integer consolidados;
    private Integer batismos;
    private Boolean multiplicou;
    private Integer pontuacao;
    private Integer posicao;

    // Construtor para converter a Projeção (Interface) em DTO (Classe)
    public RankingCelulaDTO(RankingCelulaProjection p) {
        this.celulaId = p.getCelulaId();
        this.nomeCelula = p.getNomeCelula();
        this.lider = p.getLider();
        this.presencaMedia = p.getPresencaMedia() != null ? p.getPresencaMedia().intValue() : 0;
        this.visitantes = p.getVisitantes() != null ? p.getVisitantes().intValue() : 0;
        this.consolidados = p.getConsolidados() != null ? p.getConsolidados().intValue() : 0;
        this.batismos = p.getBatismos() != null ? p.getBatismos().intValue() : 0;
        this.multiplicou = Boolean.TRUE.equals(p.getMultiplicou());
        this.pontuacao = p.getPontuacao() != null ? p.getPontuacao().intValue() : 0;
        this.posicao = 0;
    }
}