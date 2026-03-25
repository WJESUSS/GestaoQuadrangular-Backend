package com.gestaoigrejaemcelula.demo.aplication.dto;

public class MembroResumoDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String status;

    // CONSTRUTOR PADRÃO (Necessário para o Jackson/JSON)
    public MembroResumoDTO() {
    }

    // CONSTRUTOR PERSONALIZADO (O que o seu Service está chamando)
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
}