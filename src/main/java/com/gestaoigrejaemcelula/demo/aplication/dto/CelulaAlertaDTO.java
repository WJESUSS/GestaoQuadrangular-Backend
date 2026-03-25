package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CelulaAlertaDTO {

    private Long celulaId;
    private String nomeCelula;
    private String lider;
    private double mediaAtual;
    private double mediaAnterior;
    private String nivel;

}
