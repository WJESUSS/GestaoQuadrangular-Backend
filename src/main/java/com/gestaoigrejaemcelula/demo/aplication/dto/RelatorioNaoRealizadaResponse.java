package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.MotivoNaoRealizacaoCelula;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RelatorioNaoRealizadaResponse {

    private Long id;
    private Long celulaId;
    private String nomeCelula;
    private LocalDate dataReuniao;
    private Boolean realizada;
    private MotivoNaoRealizacaoCelula motivoNaoRealizacao;
    private LocalDateTime criadoEm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCelulaId() {
        return celulaId;
    }

    public void setCelulaId(Long celulaId) {
        this.celulaId = celulaId;
    }

    public String getNomeCelula() {
        return nomeCelula;
    }

    public void setNomeCelula(String nomeCelula) {
        this.nomeCelula = nomeCelula;
    }

    public LocalDate getDataReuniao() {
        return dataReuniao;
    }

    public void setDataReuniao(LocalDate dataReuniao) {
        this.dataReuniao = dataReuniao;
    }

    public Boolean getRealizada() {
        return realizada;
    }

    public void setRealizada(Boolean realizada) {
        this.realizada = realizada;
    }

    public MotivoNaoRealizacaoCelula getMotivoNaoRealizacao() {
        return motivoNaoRealizacao;
    }

    public void setMotivoNaoRealizacao(MotivoNaoRealizacaoCelula motivoNaoRealizacao) {
        this.motivoNaoRealizacao = motivoNaoRealizacao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
// getters e setters
}