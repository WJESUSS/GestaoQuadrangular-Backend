package com.gestaoigrejaemcelula.demo.aplication.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class Missao70RequestDTO {
    private String nome;
    private String nomeAnfitriao;
    private LocalTime horario;

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    private String endereco;
    private String telefoneContato;
    private LocalDate dataInicio;
    private Long celulaId;
    private Long liderId;
    private Long auxiliarId;
    private Long terceiroMembroId;

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
    public Long getTerceiroMembroId() { return terceiroMembroId; }
    public void setTerceiroMembroId(Long terceiroMembroId) { this.terceiroMembroId = terceiroMembroId; }
}