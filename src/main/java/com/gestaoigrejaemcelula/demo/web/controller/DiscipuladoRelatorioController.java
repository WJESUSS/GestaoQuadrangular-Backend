package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoHistoricoItemDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoSemanaDetalheDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioDiscipuladoDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.DiscipuladoRelatorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/discipulado")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('LIDER_CELULA', 'SECRETARIO', 'ADMIN', 'PASTOR')")
public class DiscipuladoRelatorioController {

    private final DiscipuladoRelatorioService service;

    // ── Enviar relatório semanal ─────────────────────────────────────────────
    @PostMapping("/relatorio-semanal")
    public ResponseEntity<Void> enviarRelatorioSemanal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestBody @Valid List<@Valid DiscipuladoRequestDTO> lista
    ) {
        service.salvarRelatorioSemanal(lista, inicio, fim);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ── Listar semana específica ─────────────────────────────────────────────
    @GetMapping("/relatorio-semanal")
    public ResponseEntity<List<RelatorioDiscipuladoDTO>> listarSemana(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return ResponseEntity.ok(service.listarSemana(inicio, fim));
    }

    // ── Alternativo (compatibilidade) ────────────────────────────────────────
    @PostMapping("/semana")
    public ResponseEntity<Void> salvarSemana(
            @RequestBody @Valid List<@Valid DiscipuladoRequestDTO> lista,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim
    ) {
        service.salvarRelatorioSemanal(lista, inicio, fim);
        return ResponseEntity.ok().build();
    }

    // ── Atualizar registro único ─────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<RelatorioDiscipuladoDTO> atualizarUnico(
            @PathVariable Long id,
            @RequestBody @Valid DiscipuladoRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizarRelatorio(id, dto));
    }

    // ── Histórico paginado ───────────────────────────────────────────────────
    /**
     * GET /discipulado/historico?page=0&size=10
     *
     * Retorna uma página de semanas registradas pela célula do líder logado.
     *
     * Parâmetros:
     *   page  – número da página (0-based, padrão 0)
     *   size  – itens por página (padrão 10, máximo recomendado 50)
     *
     * Resposta (Page<DiscipuladoHistoricoItemDTO>):
     * {
     *   "content": [ { "id": 1, "inicio": "2025-06-02", "fim": "2025-06-08",
     *                  "totalMembros": 12, "totalPresencas": 48,
     *                  "totalPossivel": 60, "frequencia": 80 }, ... ],
     *   "totalElements": 25,
     *   "totalPages": 3,
     *   "number": 0,
     *   "size": 10,
     *   "first": true,
     *   "last": false
     * }
     */
    @GetMapping("/historico")
    public ResponseEntity<Page<DiscipuladoHistoricoItemDTO>> listarHistorico(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Protege contra valores absurdos
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        return ResponseEntity.ok(service.listarHistorico(safePage, safeSize));
    }

    // ── Atualizar semana completa ────────────────────────────────────────────
    @PutMapping("/relatorio-semanal/{id}")
    public ResponseEntity<Void> atualizarRelatorioSemanal(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestBody @Valid List<@Valid DiscipuladoRequestDTO> lista
    ) {
        service.atualizarRelatorioSemanal(id, lista, inicio, fim);
        return ResponseEntity.ok().build();
    }

    // ── Detalhe de uma semana ────────────────────────────────────────────────
    @GetMapping("/relatorio-semanal/{id}")
    public ResponseEntity<DiscipuladoSemanaDetalheDTO> buscarDetalhe(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.buscarDetalhe(id));
    }
}