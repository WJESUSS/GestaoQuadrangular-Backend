package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MetaRequestDTO {
    @NotNull private Long celulaId;
    @NotBlank private String tipoMeta;
    @NotNull @Min(1) private Integer metaTotal;
    private Integer metaAlcancada;
    @NotNull private LocalDate mesAno;
    private boolean ativa = true;
    private String descricao;

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
    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }
    public String getTipoMeta() { return tipoMeta; }
    public void setTipoMeta(String tipoMeta) { this.tipoMeta = tipoMeta; }
    public Integer getMetaTotal() { return metaTotal; }
    public void setMetaTotal(Integer metaTotal) { this.metaTotal = metaTotal; }
    public Integer getMetaAlcancada() { return metaAlcancada; }
    public void setMetaAlcancada(Integer metaAlcancada) { this.metaAlcancada = metaAlcancada; }
    public LocalDate getMesAno() { return mesAno; }
    public void setMesAno(LocalDate mesAno) { this.mesAno = mesAno; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
