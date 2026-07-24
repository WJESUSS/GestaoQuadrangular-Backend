package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
public record CelulaResponseDTO(
        Long id,
        String nome,
        String anfitriao,
        String endereco,
        String bairro,
        DayOfWeek diaSemana,
        LocalTime horario,
        Long liderId,
        String nomeLider,
        boolean ativa,
        List<MembroDTO> membros,
        int quantidadeMembrosAtivos,
        Celula.StatusMultiplicacao statusMultiplicacao
) {

    public CelulaResponseDTO(Celula celula) {
        this(
                celula.getId(),
                celula.getNome(),
                celula.getAnfitriao(),
                celula.getEndereco(),
                celula.getBairro(),
                celula.getDiaSemana(),
                celula.getHorario(),
                celula.getLider().getId(),
                celula.getLider().getNome(),
                celula.isAtiva(),
                celula.getMembros().stream()
                        .map(MembroDTO::new)   // Agora recebe Membro
                        .toList(),
                celula.getQuantidadeMembrosAtivos(),
                celula.getStatusMultiplicacao()
        );
    }

    // Mude o construtor do MembroDTO para aceitar Membro
    public record MembroDTO(Long id, String nome, String telefone, LocalDate dataNascimento) {

        // construtor a partir da entidade Membro
        public MembroDTO(Membro m) {
            this(m.getId(), m.getNome(), m.getTelefone(), m.getDataNascimento());
        }
    }
}
