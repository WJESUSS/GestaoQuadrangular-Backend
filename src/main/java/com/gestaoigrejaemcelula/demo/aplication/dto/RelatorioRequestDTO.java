package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class RelatorioRequestDTO {

    private Long celulaId;
    private LocalDate dataReuniao;
    private String estudo;

    private List<Long> membrosPresentesIds;

    private List<VisitantePresencaDTO> visitantesPresentes;

    private Integer quantidadeVisitantes = 0;
}
