package com.gestaoigrejaemcelula.demo.aplication.dto;



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

    private StatusMissao70 status;

    /** Semanas já realizadas (0 a 4) */
    private int semanasRealizadas;

    /** Semanas ainda restantes (0 a 4) */
    private int semanasRestantes;

    /** Semana em que está atualmente (1 a 4, ou 5 se concluída) */
    private int semanaAtual;

    private int totalVisitantes;

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

    public StatusMissao70 getStatus() { return status; }
    public void setStatus(StatusMissao70 status) { this.status = status; }

    public int getSemanasRealizadas() { return semanasRealizadas; }
    public void setSemanasRealizadas(int semanasRealizadas) { this.semanasRealizadas = semanasRealizadas; }

    public int getSemanasRestantes() { return semanasRestantes; }
    public void setSemanasRestantes(int semanasRestantes) { this.semanasRestantes = semanasRestantes; }

    public int getSemanaAtual() { return semanaAtual; }
    public void setSemanaAtual(int semanaAtual) { this.semanaAtual = semanaAtual; }

    public int getTotalVisitantes() { return totalVisitantes; }
    public void setTotalVisitantes(int totalVisitantes) { this.totalVisitantes = totalVisitantes; }

    public long getTotalAceitouJesus() { return totalAceitouJesus; }
    public void setTotalAceitouJesus(long totalAceitouJesus) { this.totalAceitouJesus = totalAceitouJesus; }

    public long getTotalReconciliacao() { return totalReconciliacao; }
    public void setTotalReconciliacao(long totalReconciliacao) { this.totalReconciliacao = totalReconciliacao; }

    public long getTotalDesejoBatismo() { return totalDesejoBatismo; }
    public void setTotalDesejoBatismo(long totalDesejoBatismo) { this.totalDesejoBatismo = totalDesejoBatismo; }
}