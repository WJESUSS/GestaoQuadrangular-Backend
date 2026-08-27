package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaDTO {
    private Long          id;
    private String        entidade;
    private String        entidadeId;
    private String        entidadeNome;
    private String        acao;
    private String        detalhes;
    private String        usuarioNome;
    private String        usuarioEmail;
    private String        aprovadorNome;
    private String        aprovadorEmail;
    // AuditoriaDTO.java
    private OffsetDateTime dataHora;  // era LocalDateTime
    private String        ipOrigem;
}

