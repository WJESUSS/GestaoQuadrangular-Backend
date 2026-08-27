package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.aplication.service.CultoPdfService;
import com.gestaoigrejaemcelula.demo.aplication.service.CultoService;
import com.gestaoigrejaemcelula.demo.domain.entity.Culto;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoCulto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cultos")
public class CultoController {

    private final CultoService service;
    private final CultoPdfService pdfService;

    public CultoController(CultoService service, CultoPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    @PostMapping
    public ResponseEntity<CultoResponseDTO> criar(@RequestBody @Valid CultoRequestDTO dto) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CultoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public Page<CultoResponseDTO> listar(
            @ModelAttribute CultoFiltrosDTO filtros,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.listar(filtros, pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CultoResponseDTO> editar(
            @PathVariable UUID id,
            @RequestBody @Valid CultoRequestDTO dto) {
        return ResponseEntity.ok(service.editar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tipo-sugerido")
    public ResponseEntity<Map<String, String>> tipoSugerido(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        TipoCulto tipo = CultoService.calcularTipoCulto(data);
        return ResponseEntity.ok(Map.of(
                "data", data.toString(),
                "tipoSugerido", tipo != null ? tipo.name() : "NENHUM",
                "descricao", tipo != null ? tipo.getDescricao() : "Cadastro manual (dia sem sugestão automática)"
        ));
    }

    // ── RELATÓRIOS ──────────────────────────────────────────────────────

    @GetMapping("/relatorio/resumo")
    public ResponseEntity<CultoRelatorioResumoDTO> relatorioResumo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(service.relatorioResumo(dataInicio, dataFim));
    }

    @GetMapping("/relatorio/comparativo")
    public Page<CultoRelatorioComparativoDTO> relatorioComparativo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.relatorioComparativo(dataInicio, dataFim, pageable);
    }

    @GetMapping("/relatorio/campanhas")
    public Page<CultoRelatorioCampanhaDTO> relatorioCampanhas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.relatorioCampanhas(dataInicio, dataFim, pageable);
    }

    @GetMapping("/relatorio/pregadores")
    public Page<CultoRelatorioPregadorDTO> relatorioPregadores(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.relatorioPregadores(dataInicio, dataFim, pageable);
    }

    // ── PDF ─────────────────────────────────────────────────────────────

    @GetMapping("/{id}/pdf")
    public void pdfIndividual(@PathVariable UUID id, HttpServletResponse response) throws IOException {
        Culto culto = service.buscarCultoParaPdf(id);
        byte[] pdf = pdfService.gerarPdfIndividual(culto);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=culto-" + culto.getData() + "-" + culto.getTipoCulto() + ".pdf");
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    @GetMapping("/pdf/geral")
    public void pdfGeral(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String tipoCulto,
            @RequestParam(required = false) String pregador,
            HttpServletResponse response) throws IOException {

        TipoCulto tipo = null;
        if (tipoCulto != null && !tipoCulto.isBlank()) {
            tipo = TipoCulto.fromString(tipoCulto);
        }

        List<Culto> cultos = service.buscarCultosParaPdf(dataInicio, dataFim, tipo, pregador);
        byte[] pdf = pdfService.gerarPdfGeral(cultos, dataInicio, dataFim);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=relatorio-cultos-" + dataInicio + "-ate-" + dataFim + ".pdf");
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }
}
