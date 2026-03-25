package com.gestaoigrejaemcelula.demo.aplication.dto;

import java.time.LocalDate;
import java.util.List;

public class RelatorioDTO {
    private Long celulaId;
    private LocalDate dataReuniao;
    private String estudo;
    private List<Long> membrosPresentesIds;
    private List<Long> visitantesPresentesIds;
    private Integer quantidadeAvulsos;

    public Long getCelulaId() {
        return celulaId;
    }

    public void setCelulaId(Long celulaId) {
        this.celulaId = celulaId;
    }

    public LocalDate getDataReuniao() {
        return dataReuniao;
    }

    public void setDataReuniao(LocalDate dataReuniao) {
        this.dataReuniao = dataReuniao;
    }

    public String getEstudo() {
        return estudo;
    }

    public void setEstudo(String estudo) {
        this.estudo = estudo;
    }

    public List<Long> getMembrosPresentesIds() {
        return membrosPresentesIds;
    }

    public void setMembrosPresentesIds(List<Long> membrosPresentesIds) {
        this.membrosPresentesIds = membrosPresentesIds;
    }

    public List<Long> getVisitantesPresentesIds() {
        return visitantesPresentesIds;
    }

    public void setVisitantesPresentesIds(List<Long> visitantesPresentesIds) {
        this.visitantesPresentesIds = visitantesPresentesIds;
    }

    public Integer getQuantidadeAvulsos() {
        return quantidadeAvulsos;
    }

    public void setQuantidadeAvulsos(Integer quantidadeAvulsos) {
        this.quantidadeAvulsos = quantidadeAvulsos;
    }

    // Getters e Setters
}
