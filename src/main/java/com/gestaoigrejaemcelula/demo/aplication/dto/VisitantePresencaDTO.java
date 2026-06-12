package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitantePresencaDTO {
    private Long id;
    private DecisaoEspiritual decisaoEspiritual;
}