package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
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
                        .collect(Collectors.toList()),
                celula.getQuantidadeMembrosAtivos(),
                celula.getStatusMultiplicacao()
        );
    }

    // Mude o construtor do MembroDTO para aceitar Membro
    public record MembroDTO(Long id, String nome, String email) {
        public MembroDTO(Membro m) {   // <--- mude de Usuario para Membro
            this(m.getId(), m.getNome(), m.getEmail());
        }
    }
}