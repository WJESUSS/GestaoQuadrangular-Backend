package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.Getter;

import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class MembroSelectDTO {

    private Long id;
    private String nome;

    // Construtor para a query que passa ID e NOME
    public MembroSelectDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // Construtor para a query que passa APENAS O NOME (Exigido pelo seu erro atual)
    public MembroSelectDTO(String nome) {
        this.nome = nome;
    }
}