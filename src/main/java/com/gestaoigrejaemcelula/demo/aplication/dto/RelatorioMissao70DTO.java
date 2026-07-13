package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.MotivoCancelamentoMissao70;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;
import java.time.LocalDate;

public class RelatorioMissao70DTO {
    private Long id;
    private String nome;
    private String nomeAnfitriao;
    private String endereco;
    private LocalDate dataInicio;
    private String nomeCelula;
    private String nomeLider;
    private String nomeViceLider;
    private String nomeAuxiliar;
    private String nomeTerceiroMembro;
    private StatusMissao70 status;

    // --- Cancelamento ---
    private MotivoCancelamentoMissao70 motivoCancelamento;
    private String motivoCancelamentoDescricao;
    private String observacaoCancelamento;

    /** Semanas já realizadas (0 a 4) */
    private int semanasRealizadas;
    /** Semanas ainda restantes (0 a 4) */
    private int semanasRestantes;
    /** Semana em que está atualmente (1 a 4, ou 5 se concluída) */
    private int semanaAtual;
    /** Visitantes únicos da missão (mesma pessoa conta 1x, mesmo que tenha ido a vários cultos) */
    private int totalVisitantes;
    /** Soma de presenças por culto (mesma pessoa conta 1x por semana em que participou) */
    private int totalVisitantesPorCulto;
    private long totalAceitouJesus;
    private long totalReconciliacao;
    private long totalDesejoBatismo;

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getNomeAnfitriao() { return nomeAnfitriao; }
    public void setNomeAnfitriao(String nomeAnfitriao) { this.nomeAnfitriao = nomeAnfitriao; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public String getNomeCelula() { return nomeCelula; }
    public void setNomeCelula(String nomeCelula) { this.nomeCelula = nomeCelula; }
    public String getNomeLider() { return nomeLider; }
    public void setNomeLider(String nomeLider) { this.nomeLider = nomeLider; }
    public String getNomeViceLider() { return nomeViceLider; }
    public void setNomeViceLider(String nomeViceLider) { this.nomeViceLider = nomeViceLider; }
    public String getNomeAuxiliar() { return nomeAuxiliar; }
    public void setNomeAuxiliar(String nomeAuxiliar) { this.nomeAuxiliar = nomeAuxiliar; }
    public String getNomeTerceiroMembro() { return nomeTerceiroMembro; }
    public void setNomeTerceiroMembro(String nomeTerceiroMembro) { this.nomeTerceiroMembro = nomeTerceiroMembro; }
    public StatusMissao70 getStatus() { return status; }
    public void setStatus(StatusMissao70 status) { this.status = status; }

    public MotivoCancelamentoMissao70 getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(MotivoCancelamentoMissao70 motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }
    public String getMotivoCancelamentoDescricao() { return motivoCancelamentoDescricao; }
    public void setMotivoCancelamentoDescricao(String motivoCancelamentoDescricao) { this.motivoCancelamentoDescricao = motivoCancelamentoDescricao; }
    public String getObservacaoCancelamento() { return observacaoCancelamento; }
    public void setObservacaoCancelamento(String observacaoCancelamento) { this.observacaoCancelamento = observacaoCancelamento; }

    public int getSemanasRealizadas() { return semanasRealizadas; }
    public void setSemanasRealizadas(int semanasRealizadas) { this.semanasRealizadas = semanasRealizadas; }
    public int getSemanasRestantes() { return semanasRestantes; }
    public void setSemanasRestantes(int semanasRestantes) { this.semanasRestantes = semanasRestantes; }
    public int getSemanaAtual() { return semanaAtual; }
    public void setSemanaAtual(int semanaAtual) { this.semanaAtual = semanaAtual; }
    public int getTotalVisitantes() { return totalVisitantes; }
    public void setTotalVisitantes(int totalVisitantes) { this.totalVisitantes = totalVisitantes; }
    public int getTotalVisitantesPorCulto() { return totalVisitantesPorCulto; }
    public void setTotalVisitantesPorCulto(int totalVisitantesPorCulto) { this.totalVisitantesPorCulto = totalVisitantesPorCulto; }
    public long getTotalAceitouJesus() { return totalAceitouJesus; }
    public void setTotalAceitouJesus(long totalAceitouJesus) { this.totalAceitouJesus = totalAceitouJesus; }
    public long getTotalReconciliacao() { return totalReconciliacao; }
    public void setTotalReconciliacao(long totalReconciliacao) { this.totalReconciliacao = totalReconciliacao; }
    public long getTotalDesejoBatismo() { return totalDesejoBatismo; }
    public void setTotalDesejoBatismo(long totalDesejoBatismo) { this.totalDesejoBatismo = totalDesejoBatismo; }
}