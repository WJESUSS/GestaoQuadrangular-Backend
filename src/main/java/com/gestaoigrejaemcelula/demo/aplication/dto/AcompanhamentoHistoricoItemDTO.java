package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcompanhamentoHistoricoItemDTO {

    private Long id;
    private String tipo;

    private Long celulaId;
    private String celulaNome;

    private LocalDate data;

    private Long membroId;
    private String membroNome;
    private Integer quantidadeParticipantes;

    private String tema;
    private String tipoEstudoDescricao;

    private Long liderId;
    private String liderNome;

    private String status;
    private Integer pontos;
}
