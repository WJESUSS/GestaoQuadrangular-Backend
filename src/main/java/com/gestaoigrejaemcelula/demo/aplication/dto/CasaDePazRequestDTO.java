package com.gestaoigrejaemcelula.demo.aplication.dto;

import java.time.LocalDate;

public class CasaDePazRequestDTO {
    private String nome;
    private String nomeAnfitriao;
    private String endereco;
    private String telefoneContato;
    private LocalDate dataInicio;
    private Long celulaId;
    private Long liderId;
    private Long auxiliarId;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getNomeAnfitriao() { return nomeAnfitriao; }
    public void setNomeAnfitriao(String nomeAnfitriao) { this.nomeAnfitriao = nomeAnfitriao; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }
    public Long getLiderId() { return liderId; }
    public void setLiderId(Long liderId) { this.liderId = liderId; }
    public Long getAuxiliarId() { return auxiliarId; }
    public void setAuxiliarId(Long auxiliarId) { this.auxiliarId = auxiliarId; }
}