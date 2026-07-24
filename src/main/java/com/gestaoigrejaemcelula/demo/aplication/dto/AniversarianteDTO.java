package com.gestaoigrejaemcelula.demo.aplication.dto;

public class AniversarianteDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String linkWhatsApp;

    public AniversarianteDTO(Long id, String nome, String telefone, String linkWhatsApp) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.linkWhatsApp = linkWhatsApp;
    }

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

    public String getLinkWhatsApp() {
        return linkWhatsApp;
    }

    public void setLinkWhatsApp(String linkWhatsApp) {
        this.linkWhatsApp = linkWhatsApp;
    }
// getters
}