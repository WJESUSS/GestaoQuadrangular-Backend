package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.StatusCasaDePaz;

public class RelatorioCasaDePazDTO {
    private Long id;
    private String nome;
    private String nomeCelula;
    private String nomeLider;
    private String nomeAuxiliar;
    private StatusCasaDePaz status;
    private int encontrosRealizados;
    private int encontrosRestantes;
    private int totalVisitantes;
    private long totalAceitouJesus;
    private long totalReconciliacao;
    private long totalDesejoBatismo;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getNomeCelula() { return nomeCelula; }
    public void setNomeCelula(String nomeCelula) { this.nomeCelula = nomeCelula; }
    public String getNomeLider() { return nomeLider; }
    public void setNomeLider(String nomeLider) { this.nomeLider = nomeLider; }
    public String getNomeAuxiliar() { return nomeAuxiliar; }
    public void setNomeAuxiliar(String nomeAuxiliar) { this.nomeAuxiliar = nomeAuxiliar; }
    public StatusCasaDePaz getStatus() { return status; }
    public void setStatus(StatusCasaDePaz status) { this.status = status; }
    public int getEncontrosRealizados() { return encontrosRealizados; }
    public void setEncontrosRealizados(int encontrosRealizados) { this.encontrosRealizados = encontrosRealizados; }
    public int getEncontrosRestantes() { return encontrosRestantes; }
    public void setEncontrosRestantes(int encontrosRestantes) { this.encontrosRestantes = encontrosRestantes; }
    public int getTotalVisitantes() { return totalVisitantes; }
    public void setTotalVisitantes(int totalVisitantes) { this.totalVisitantes = totalVisitantes; }
    public long getTotalAceitouJesus() { return totalAceitouJesus; }
    public void setTotalAceitouJesus(long totalAceitouJesus) { this.totalAceitouJesus = totalAceitouJesus; }
    public long getTotalReconciliacao() { return totalReconciliacao; }
    public void setTotalReconciliacao(long totalReconciliacao) { this.totalReconciliacao = totalReconciliacao; }
    public long getTotalDesejoBatismo() { return totalDesejoBatismo; }
    public void setTotalDesejoBatismo(long totalDesejoBatismo) { this.totalDesejoBatismo = totalDesejoBatismo; }
}