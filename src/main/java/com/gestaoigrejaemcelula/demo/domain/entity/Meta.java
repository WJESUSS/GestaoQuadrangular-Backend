package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "metas")
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celula_id", nullable = false)
    private Celula celula;

    @Column(nullable = false)
    private String tipoMeta; // BATISMO, CONVERSAO, RECONCILIACAO, DISCIPULADO

    @Column(nullable = false)
    private Integer metaTotal; // Número total esperado (ex: 3 pessoas)

    @Column(nullable = false)
    private Integer metaAlcancada = 0; // Contador atual

    @Column(nullable = false)
    private LocalDate mesAno; // Mês/ano da meta (ex: 2024-12)

    @Column(nullable = false)
    private boolean ativa = true;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao = LocalDate.now();

    // Campos auxiliares
    private String descricao; // Descrição opcional da meta

    // --- MÉTODOS AUXILIARES ---

    /**
     * Retorna o progresso em percentual
     */
    public Integer getProgressoPercentual() {
        if (metaTotal == 0) return 0;
        return (metaAlcancada * 100) / metaTotal;
    }

    /**
     * Retorna quantos faltam para atingir a meta
     */
    public Integer getFaltam() {
        return Math.max(0, metaTotal - metaAlcancada);
    }

    /**
     * Verifica se a meta foi atingida
     */
    public boolean isMetaConcluida() {
        return metaAlcancada >= metaTotal;
    }

    /**
     * Incrementa o contador de metas alcançadas
     */
    public void incrementarProgresso() {
        if (!isMetaConcluida()) {
            this.metaAlcancada++;
        }
    }

    /**
     * Decrementa o contador de metas alcançadas
     */
    public void decrementarProgresso() {
        if (metaAlcancada > 0) {
            this.metaAlcancada--;
        }
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Celula getCelula() {
        return celula;
    }

    public void setCelula(Celula celula) {
        this.celula = celula;
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

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}