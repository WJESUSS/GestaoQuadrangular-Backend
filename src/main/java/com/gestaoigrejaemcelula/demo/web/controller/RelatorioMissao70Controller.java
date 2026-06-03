package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioMissao70DTO;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioMissao70Service;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pastor/missao70")
public class RelatorioMissao70Controller {

    private final RelatorioMissao70Service service;

    public RelatorioMissao70Controller(RelatorioMissao70Service service) {
        this.service = service;
    }

    /**
     * GET /api/pastor/missao70/relatorio
     * Lista todas as missões com resumo para o pastor.
     *
     * Filtros opcionais:
     *   ?celulaId=1
     *   ?status=EM_ANDAMENTO  (EM_ANDAMENTO | CONCLUIDA | CANCELADA)
     *   ?dataInicio=2025-06-01&dataFim=2025-06-30
     */
    @GetMapping("/relatorio")
    public ResponseEntity<List<RelatorioMissao70DTO>> relatorio(
            @RequestParam(required = false) Long celulaId,
            @RequestParam(required = false) StatusMissao70 status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        return ResponseEntity.ok(service.gerarRelatorio(celulaId, status, dataInicio, dataFim));
    }

    /**
     * GET /api/pastor/missao70/relatorio/{id}
     * Relatório detalhado de uma única Missão 70.
     */
    @GetMapping("/relatorio/{id}")
    public ResponseEntity<RelatorioMissao70DTO> relatorioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.gerarRelatorioPorId(id));
    }

    /**
     * GET /api/pastor/missao70/resumo
     * Totais gerais de todas as missões — painel do pastor.
     *
     * Retorna:
     *   total, emAndamento, concluidas, canceladas,
     *   totalVisitantes, totalAceitouJesus, totalReconciliacao, totalBatismo
     */
    @GetMapping("/resumo")
    public ResponseEntity<RelatorioMissao70Service.ResumoGeralMissao70> resumoGeral() {
        return ResponseEntity.ok(service.gerarResumoGeral());
    }
}