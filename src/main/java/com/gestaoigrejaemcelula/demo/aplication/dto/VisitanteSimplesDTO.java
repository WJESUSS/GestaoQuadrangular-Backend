package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.Data;

@Data
public class VisitanteSimplesDTO {
    private Long id;
    private String nome;

    public VisitanteSimplesDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}