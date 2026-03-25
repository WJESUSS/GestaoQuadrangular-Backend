package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;

public record PessoaPresencaDTO(Long id, String nome, DecisaoEspiritual decisaoEspiritual) {}
