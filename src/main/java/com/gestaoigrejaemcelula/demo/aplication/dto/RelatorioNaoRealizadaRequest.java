package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.MotivoNaoRealizacaoCelula;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RelatorioNaoRealizadaRequest {

    @NotNull
    private Long celulaId;

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

    public MotivoNaoRealizacaoCelula getMotivoNaoRealizacao() {
        return motivoNaoRealizacao;
    }

    public void setMotivoNaoRealizacao(MotivoNaoRealizacaoCelula motivoNaoRealizacao) {
        this.motivoNaoRealizacao = motivoNaoRealizacao;
    }

    @NotNull
    private LocalDate dataReuniao;

    @NotNull
    private MotivoNaoRealizacaoCelula motivoNaoRealizacao;

    // getters e setters
}
