package com.gestaoigrejaemcelula.demo.aplication.dto;

import java.time.LocalDate;

public class MetaRequestDTO {

    private Long celulaId;
    private String tipoMeta; // BATISMO, CONVERSAO, RECONCILIACAO, DISCIPULADO
    private Integer metaTotal; // Número total esperado
    private Integer metaAlcancada; // Valor inicial (normalmente 0)
    private LocalDate mesAno; // Mês/ano da meta
    private boolean ativa = true;
    private String descricao;

    // Construtores
    public MetaRequestDTO() {}

    public MetaRequestDTO(Long celulaId, String tipoMeta, Integer metaTotal,
                          LocalDate mesAno, String descricao) {
        this.celulaId = celulaId;
        this.tipoMeta = tipoMeta;
        this.metaTotal = metaTotal;
        this.mesAno = mesAno;
        this.metaAlcancada = 0;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Long getCelulaId() {
        return celulaId;
    }

    public void setCelulaId(Long celulaId) {
        this.celulaId = celulaId;
    }

    public String getTipoMeta() {
        return tipoMeta;
    }

    public void setTipoMeta(String tipoMeta) {
        this.tipoMeta = tipoMeta;
    }

    public Integer getMetaTotal() {
        return metaTotal;
    }

    public void setMetaTotal(Integer metaTotal) {
        this.metaTotal = metaTotal;
    }

    public Integer getMetaAlcancada() {
        return metaAlcancada;
    }

    public void setMetaAlcancada(Integer metaAlcancada) {
        this.metaAlcancada = metaAlcancada;
    }

    public LocalDate getMesAno() {
        return mesAno;
    }

    public void setMesAno(LocalDate mesAno) {
        this.mesAno = mesAno;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}