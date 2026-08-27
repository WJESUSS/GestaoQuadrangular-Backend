package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Culto;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoCulto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CultoResponseDTO(
        UUID id,
        LocalDate data,
        TipoCulto tipoCulto,
        String tipoCultoDescricao,
        String horario,
        String textoPregado,
        String pregador,
        Integer quantidadeMembros,
        Integer visitantesSimpatizantes,
        Integer totalCriancas,
        Integer quantidadeDiaconos,
        Integer totalGeral,
        Boolean campanhaAtiva,
        String nomeCampanha,
        String observacoes,
        Long registradoPorId,
        String registradoPorNome,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public CultoResponseDTO(Culto c) {
        this(
                c.getId(),
                c.getData(),
                c.getTipoCulto(),
                c.getTipoCulto() != null ? c.getTipoCulto().getDescricao() : null,
                c.getHorario(),
                c.getTextoPregado(),
                c.getPregador(),
                c.getQuantidadeMembros(),
                c.getVisitantesSimpatizantes(),
                c.getTotalCriancas(),
                c.getQuantidadeDiaconos(),
                c.getTotalGeral(),
                c.getCampanhaAtiva(),
                c.getNomeCampanha(),
                c.getObservacoes(),
                c.getRegistradoPor() != null ? c.getRegistradoPor().getId() : null,
                c.getRegistradoPor() != null ? c.getRegistradoPor().getNome() : null,
                c.getCriadoEm(),
                c.getAtualizadoEm()
        );
    }
}
