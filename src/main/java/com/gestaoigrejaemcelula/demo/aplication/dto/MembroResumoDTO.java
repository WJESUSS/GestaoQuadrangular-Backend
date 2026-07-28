package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.CargoMembro;

import java.util.Set;

public class MembroResumoDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String status;
    private Set<CargoMembro> cargos;

    // CONSTRUTOR PADRÃO (Necessário para o Jackson/JSON)
    public MembroResumoDTO() {
    }

    // CONSTRUTOR PERSONALIZADO (usado por JPQL em MembroRepository)
    public MembroResumoDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // Getters e Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Set<CargoMembro> getCargos() { return cargos; }
    public void setCargos(Set<CargoMembro> cargos) { this.cargos = cargos; }
}