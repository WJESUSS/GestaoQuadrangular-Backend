package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import java.time.LocalDate;

public record MembroResponseDTO(
        Long id,
        String nome,
        String email,          // Adicionado
        String telefone,       // Adicionado
        String endereco,       // Adicionado
        LocalDate dataNascimento, // Adicionado
        LocalDate dataConversao,  // Adicionado
        LocalDate dataBatismo,    // Adicionado
        StatusMembro status,
        Long celulaId,
        String nomeCelula,
        Long liderId,
        String nomeLider,String cpf,
        EstadoCivil estadoCivil

        ) {
    public MembroResponseDTO(Membro m) {
        this(
                m.getId(),
                m.getNome(),
                m.getEmail(),
                m.getTelefone(),
                m.getEndereco(),
                m.getDataNascimento(),
                m.getDataConversao(),
                m.getDataBatismo(),
                m.getStatus(),
                m.getCelula() != null ? m.getCelula().getId() : null,
                m.getCelula() != null ? m.getCelula().getNome() : "Sem célula",
                m.getCelula() != null && m.getCelula().getLider() != null
                        ? m.getCelula().getLider().getId() : null,
                m.getCelula() != null && m.getCelula().getLider() != null
                        ? m.getCelula().getLider().getNome() : "Sem líder",
                m.getCpf(),
                m.getEstadoCivil()
        );
    }
}