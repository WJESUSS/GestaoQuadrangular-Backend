package com.gestaoigrejaemcelula.demo.aplication.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Retornado por GET /discipulado/historico
 * Item resumido exibido na aba "Histórico" do frontend.
 */
@Data
@Builder
public class DiscipuladoHistoricoItemDTO {
    private Long id;
    private LocalDate inicio;
    private LocalDate fim;
    private int totalMembros;    // ← novo
    private int totalPresencas;
    private int totalPossivel;   // ← novo
    private int frequencia;
    private int totalPontos;     // quarta=2, quinta=2, domingo=4, escola bíblica=5
}