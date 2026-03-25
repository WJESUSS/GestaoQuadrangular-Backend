package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "discipulado_acompanhamento")
public class DiscipuladoAcompanhamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Membro membro;

    private String mesReferencia; // Ex: "2026-01"
    private LocalDate dataAcao;

    // Getters e Setters...

    public boolean isAcompanhado() {
        return acompanhado;
    }

    public void setAcompanhado(boolean acompanhado) {
        this.acompanhado = acompanhado;
    }

    private boolean acompanhado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Membro getMembro() {
        return membro;
    }

    public void setMembro(Membro membro) {
        this.membro = membro;
    }

    public String getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(String mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public LocalDate getDataAcao() {
        return dataAcao;
    }

    public void setDataAcao(LocalDate dataAcao) {
        this.dataAcao = dataAcao;
    }
}
