package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.CargoMembro;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
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


}