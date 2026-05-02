package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioResumoDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioPdfService;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@PreAuthorize("hasAnyRole('LIDER_CELULA', 'ADMIN', 'SECRETARIO')")
public class RelatorioController {

    private final RelatorioService service;
    private final RelatorioPdfService pdfService;

    public RelatorioController(RelatorioService relatorioService,
                               RelatorioPdfService pdfService) {
        this.service = relatorioService;
        this.pdfService = pdfService;
    }

    // Criar relatório
    @PostMapping
    @PreAuthorize("hasRole('LIDER_CELULA')")
    public ResponseEntity<String> criar(@RequestBody @Valid RelatorioRequestDTO dto) {
        try {
            service.salvarRelatorio(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Relatório criado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao criar relatório: " + e.getMessage());
        }
    }

    // Listar relatórios da semana atual
    @GetMapping("/semana-atual")
    public ResponseEntity<List<RelatorioResponseDTO>> listarRelatoriosDaSemana() {
        List<RelatorioResponseDTO> dtos = service.listarRelatoriosUltimosSeteDias();
        return ResponseEntity.ok(dtos);
    }

    // Listar todos os relatórios
    @GetMapping
    public ResponseEntity<List<RelatorioResponseDTO>> listarTodos() {
        List<RelatorioResponseDTO> dtos = service.listarTodosComoDTO();
        return ResponseEntity.ok(dtos);
    }

    // Listar por célula
    @PreAuthorize("hasAnyRole('LIDER_CELULA', 'ADMIN', 'SECRETARIO')")
    @GetMapping("/celulas/{id}")
    public ResponseEntity<List<RelatorioResponseDTO>> listarPorCelula(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorCelula(id));
    }

    // Gerar PDF
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> gerarPdf() {

        byte[] pdf = pdfService.gerarPdf(
                service.listarTodosComoDTO()
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=relatorios.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // Buscar resumo por semana
    @PreAuthorize("hasAnyRole('ADMIN', 'PASTOR', 'SECRETARIO')")
    @GetMapping("/semana")
    public ResponseEntity<RelatorioResumoDTO> buscarPorSemana(
            @RequestParam("inicio") String inicioStr,
            @RequestParam("fim") String fimStr) {

        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);

            RelatorioResumoDTO resumo = service.buscarResumoSemana(inicio, fim);
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de data inválido. Use YYYY-MM-DD");
        }
    }
    @GetMapping("/historico")
    public List<RelatorioResponseDTO> listarHistorico(Authentication authentication) {
        String email = authentication.getName();
        return service.listarHistoricoDaMinhaCelula(email);
    }
}