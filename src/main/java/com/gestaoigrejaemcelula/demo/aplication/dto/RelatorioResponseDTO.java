package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RelatorioResponseDTO {

    private Long id;

    private Long celulaId;
    private String nomeCelula;
    private String nomeLider;

    private LocalDate dataReuniao;
    private String estudo;

    // =========================
    // PRESENÇAS
    // =========================

    private List<PessoaPresencaDTO> membrosPresentes;
    private List<PessoaPresencaDTO> visitantesPresentes;

    // =========================
    // CONTADORES
    // =========================

    private Integer quantidadeVisitantes; // avulsos (manual)

    private Integer totalMembros;
    private Integer totalVisitantes;
    private Integer totalPresentes;
}
