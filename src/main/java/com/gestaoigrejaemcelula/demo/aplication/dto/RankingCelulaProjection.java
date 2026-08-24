package com.gestaoigrejaemcelula.demo.aplication.dto;

/**
 * Interface que espelha exatamente os nomes das colunas do SELECT SQL.
 */
public interface RankingCelulaProjection {
    Long getCelulaId();
    String getNomeCelula();
    String getLider();
    Number getPresencaMedia();
    Number getVisitantes();
    Number getConsolidados();
    Number getBatismos();
    Boolean getMultiplicou();
    Number getPontuacao();
    Number getAceitouJesus();
    Number getDesejaBatismo();
    Number getReconciliou();

    default Number getPontosDiscipulado() {
        return 0;
    }
}