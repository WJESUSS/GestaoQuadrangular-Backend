package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.enums.JustificativaFalta;

public record PessoaPresencaDTO(
        Long id,
        String nome,
        DecisaoEspiritual decisaoEspiritual,
        JustificativaFalta justificativaFalta
) {
    public PessoaPresencaDTO(Long id, String nome, DecisaoEspiritual decisaoEspiritual) {
        this(id, nome, decisaoEspiritual, null);
    }
}