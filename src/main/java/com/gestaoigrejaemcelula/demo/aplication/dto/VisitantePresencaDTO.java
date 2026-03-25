package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitantePresencaDTO {

    private Long id;
    private DecisaoEspiritual decisaoEspiritual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DecisaoEspiritual getDecisaoEspiritual() {
        return decisaoEspiritual;
    }

    public void setDecisaoEspiritual(DecisaoEspiritual decisaoEspiritual) {
        this.decisaoEspiritual = decisaoEspiritual;
    }
}

