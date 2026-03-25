package com.gestaoigrejaemcelula.demo.aplication.dto;


import com.gestaoigrejaemcelula.demo.domain.entity.Celula;

public record CelulaResumoDTO(
        Long id,
        String nome,
        String liderNome,
        int qtdMembros,
        String motivoSolicitacao,
        String statusMultiplicacao
) {
    // Construtor de conveniência para converter a Entidade para DTO facilmente
    public CelulaResumoDTO(Celula celula) {
        this(
                celula.getId(),
                celula.getNome(),
                celula.getLider() != null ? celula.getLider().getNome() : "Sem Líder",
                celula.getQuantidadeMembrosAtivos(),
                celula.getMotivoSolicitacao(),
                celula.getStatusMultiplicacao().name()
        );
    }
}