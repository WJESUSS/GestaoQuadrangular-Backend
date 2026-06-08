package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioPdfService;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@PreAuthorize("hasAnyRole('LIDER_CELULA', 'ADMIN', 'SECRETARIO', 'PASTOR')")  // ✅ adicionado PASTOR
public class RelatorioController {

    private static final Logger log = LoggerFactory.getLogger(RelatorioController.class);

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
    public ResponseEntity<?> buscarPorSemana(
            @RequestParam("inicio") String inicioStr,
            @RequestParam("fim") String fimStr) {

        LocalDate inicio, fim;
        try {
            inicio = LocalDate.parse(inicioStr);
            fim = LocalDate.parse(fimStr);
        } catch (DateTimeParseException e) {
            // Log para confirmar o que está chegando
            log.warn("Formato de data inválido - inicio: '{}', fim: '{}'", inicioStr, fimStr);
            return ResponseEntity
                    .badRequest()
                    .body("Formato de data inválido. Use YYYY-MM-DD. Recebido: inicio=" + inicioStr + ", fim=" + fimStr);
        }

        RelatorioResumoDTO resumo = service.buscarResumoSemana(inicio, fim);
        return ResponseEntity.ok(resumo);
    }
    @GetMapping("/historico")
    public List<RelatorioResponseDTO> listarHistorico(Authentication authentication) {
        String email = authentication.getName();
        return service.listarHistoricoDaMinhaCelula(email);
    }
    // Sem @PreAuthorize — o SecurityConfig já garante o acesso correto
    @GetMapping("/todos-relatorios")
    public ResponseEntity<List<RelatorioDiscipuladoDTO>> buscarTodos() {
        return ResponseEntity.ok(service.listarTodosOsRelatorios());
    }
    // Editar relatório existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIDER_CELULA')")
    public ResponseEntity<String> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RelatorioRequestDTO dto) {
        try {
            service.atualizarRelatorio(id, dto);
            return ResponseEntity.ok("Relatório atualizado com sucesso!");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao atualizar relatório: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIDER_CELULA', 'ADMIN', 'SECRETARIO', 'PASTOR')")
    public ResponseEntity<RelatorioResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/nao-realizada")
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<RelatorioNaoRealizadaResponse> registrarNaoRealizada(
            @RequestBody @Valid RelatorioNaoRealizadaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        RelatorioNaoRealizadaResponse response =
                service.registrarNaoRealizada(request, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}