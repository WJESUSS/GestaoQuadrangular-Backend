package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.StatusAcompanhamentoDiscipulado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcompanhamentoIndividualResponseDTO {

    private Long id;
    private Long membroId;
    private String membroNome;
    private Long liderId;
    private String liderNome;
    private Long celulaId;
    private String celulaNome;
    private LocalDate data;
    private LocalTime horario;
    private String tipoEstudo;
    private String tipoEstudoDescricao;
    private String tipoEstudoOutro;
    private String tema;
    private String observacoes;
    private String local;
    private StatusAcompanhamentoDiscipulado status;
    private Integer pontosGerados;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private String mensagem;
}
