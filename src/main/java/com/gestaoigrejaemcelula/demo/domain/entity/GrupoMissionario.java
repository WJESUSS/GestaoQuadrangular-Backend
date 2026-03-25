package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grupo_missionario")
public class GrupoMissionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

