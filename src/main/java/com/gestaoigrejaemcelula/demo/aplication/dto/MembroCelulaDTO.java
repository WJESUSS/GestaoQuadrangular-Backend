package com.gestaoigrejaemcelula.demo.aplication.dto;


import com.gestaoigrejaemcelula.demo.domain.enums.Tipo;

public class MembroCelulaDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String status;
    private Tipo tipo; // Use o Enum aqui!

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
// Getters e Setters
}
