package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.StatusAcompanhamentoDiscipulado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcompanhamentoColetivoResponseDTO {

    private Long id;
    private Long liderId;
    private String liderNome;
    private Long celulaId;
    private String celulaNome;
    private LocalDate data;
    private LocalTime horario;
    private String tipoEstudo;
    private String tipoEstudoDescricao;
    private String tipoEstudoOutro;
    private String tema;
    private String local;
    private String observacoes;
    private StatusAcompanhamentoDiscipulado status;
    private List<AcompanhamentoParticipanteItemDTO> presentes;
    private Integer quantidadePresentes;
    private Integer pontosGerados;
    private String formulaPontuacao;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private String mensagem;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcompanhamentoParticipanteItemDTO {
        private Long membroId;
        private String membroNome;
    }
}
