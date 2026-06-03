package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Meta;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MetaResponseDTO {

    private Long id;
    private Long celulaId;
    private String celulanome;
    private String tipoMeta;
    private Integer metaTotal;
    private Integer metaAlcancada;
    private Integer faltam;
    private Integer progressoPercentual;
    private LocalDate mesAno;
    private boolean ativa;
    private LocalDate dataCriacao;
    private String descricao;
    private boolean metaConcluida;
    private long diasRestantes;   // ← NOVO
    private String statusPrazo;   // ← NOVO

    public MetaResponseDTO() {}

    public MetaResponseDTO(Meta meta) {
        this.id = meta.getId();
        this.celulaId = meta.getCelula().getId();
        this.celulanome = meta.getCelula().getNome();
        this.tipoMeta = meta.getTipoMeta();
        this.metaTotal = meta.getMetaTotal();
        this.metaAlcancada = meta.getMetaAlcancada();
        this.faltam = meta.getFaltam();
        this.progressoPercentual = meta.getProgressoPercentual();
        this.mesAno = meta.getMesAno();
        this.ativa = meta.isAtiva();
        this.dataCriacao = meta.getDataCriacao();
        this.descricao = meta.getDescricao();
        this.metaConcluida = meta.isMetaConcluida();
        this.diasRestantes = calcularDiasRestantes(meta.getMesAno()); // ← NOVO
        this.statusPrazo = calcularStatusPrazo(this.diasRestantes);   // ← NOVO
    }

    // ─────────────────────────────────────────────────────────────
    // Métodos privados de cálculo
    // ─────────────────────────────────────────────────────────────

    private long calcularDiasRestantes(LocalDate mesAno) {
        if (mesAno == null) return 0;
        // mesAno é o primeiro dia do mês → pega o último dia
        LocalDate fimDoMes = mesAno.withDayOfMonth(mesAno.lengthOfMonth());
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), fimDoMes);
        return dias < 0 ? 0 : dias;
    }

    private String calcularStatusPrazo(long dias) {
        if (dias == 0) return "Último dia!";
        if (dias <= 7) return "⚠️ Apenas " + dias + " dias restantes!";
        return dias + " dias restantes";
    }

    // ─────────────────────────────────────────────────────────────
    // Getters e Setters existentes
    // ─────────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }

    public String getCelulanome() { return celulanome; }
    public void setCelulanome(String celulanome) { this.celulanome = celulanome; }

    public String getTipoMeta() { return tipoMeta; }
    public void setTipoMeta(String tipoMeta) { this.tipoMeta = tipoMeta; }

    public Integer getMetaTotal() { return metaTotal; }
    public void setMetaTotal(Integer metaTotal) { this.metaTotal = metaTotal; }

    public Integer getMetaAlcancada() { return metaAlcancada; }
    public void setMetaAlcancada(Integer metaAlcancada) { this.metaAlcancada = metaAlcancada; }

    public Integer getFaltam() { return faltam; }
    public void setFaltam(Integer faltam) { this.faltam = faltam; }

    public Integer getProgressoPercentual() { return progressoPercentual; }
    public void setProgressoPercentual(Integer progressoPercentual) { this.progressoPercentual = progressoPercentual; }

    public LocalDate getMesAno() { return mesAno; }
    public void setMesAno(LocalDate mesAno) { this.mesAno = mesAno; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isMetaConcluida() { return metaConcluida; }
    public void setMetaConcluida(boolean metaConcluida) { this.metaConcluida = metaConcluida; }

    // ─── Novos getters ───
    public long getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(long diasRestantes) { this.diasRestantes = diasRestantes; }

    public String getStatusPrazo() { return statusPrazo; }
    public void setStatusPrazo(String statusPrazo) { this.statusPrazo = statusPrazo; }
}