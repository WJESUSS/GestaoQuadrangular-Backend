package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CultoFiltrosDTO {
    private String dataInicio;
    private String dataFim;
    private String tipoCulto;
    private String pregador;
    private Boolean campanha;
    private Long registradoPor;
}
