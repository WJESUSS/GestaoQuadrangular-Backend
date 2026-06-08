package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.MotivoNaoRealizacaoCelula;

import java.time.LocalDate;

public class RelatorioHistoricoResponse {
    private Long id;
    private LocalDate dataReuniao;
    private String estudo;
    private Integer totalPresentes;

    public Boolean getRealizada() {
        return realizada;
    }

    public void setRealizada(Boolean realizada) {
        this.realizada = realizada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getTotalPresentes() {
        return totalPresentes;
    }

    public void setTotalPresentes(Integer totalPresentes) {
        this.totalPresentes = totalPresentes;
    }

    public MotivoNaoRealizacaoCelula getMotivoNaoRealizacao() {
        return motivoNaoRealizacao;
    }

    public void setMotivoNaoRealizacao(MotivoNaoRealizacaoCelula motivoNaoRealizacao) {
        this.motivoNaoRealizacao = motivoNaoRealizacao;
    }

    private Boolean realizada;                          // NOVO
    private MotivoNaoRealizacaoCelula motivoNaoRealizacao; // NOVO
    // getters e setters
}