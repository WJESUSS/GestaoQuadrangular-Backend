package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Detalhe completo de uma semana — retornado por:
 *   GET /discipulado/relatorio-semanal/{id}
 *
 * Usado pelo frontend para:
 *  1. Expandir o card no histórico (tabela de ✅/❌ por membro)
 *  2. Popular o formulário de edição com as presenças existentes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscipuladoSemanaDetalheDTO {

    /** ID de referência (primeiro registro encontrado da semana) */
    private Long id;

    /** Nome da célula */
    private String nomeCelula;

    /** Início da semana */
    private LocalDate inicio;

    /** Fim da semana */
    private LocalDate fim;

    /**
     * Lista resumida de membros (id + nome).
     * Usada pelo frontend para montar o mapa membroId → nomeMembro
     * ao renderizar a tabela de presenças.
     */
    private List<MembroResumoDTO> membros;

    /**
     * Lista de presenças — mesma estrutura de DiscipuladoRequestDTO,
     * para que o frontend possa reusá-la diretamente no formulário.
     */
    private List<DiscipuladoRequestDTO> presencas;

    // ── DTO interno ──────────────────────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembroResumoDTO {
        private Long   id;
        private String nome;
    }
}