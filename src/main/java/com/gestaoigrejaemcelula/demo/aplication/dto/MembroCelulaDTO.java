package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.CargoMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.Tipo;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class MembroCelulaDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String status;
    private Set<CargoMembro> cargos;
    private Tipo tipo;

}
