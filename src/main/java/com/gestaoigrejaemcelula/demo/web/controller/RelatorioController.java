package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioPdfService;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
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
@PreAuthorize("hasAnyRole('LIDER_CELULA', 'ADMIN', 'SECRETARIO', 'PASTOR')")
public class RelatorioController {

    private static final Logger log = LoggerFactory.getLogger(RelatorioController.class);

    private final RelatorioService service;
    private final RelatorioPdfService pdfService;

    public RelatorioController(RelatorioService service, RelatorioPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    // ── Criar ────────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('LIDER_CELULA')")
    public ResponseEntity<String> criar(@RequestBody @Valid RelatorioRequestDTO dto) {
        try {
            service.salvarRelatorio(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Relatório criado com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao criar relatório", e);
            return ResponseEntity.badRequest().body("Erro ao criar relatório: " + e.getMessage());
        }
    }

    // ── Listagens ────────────────────────────────────────────────────────────

    @GetMapping("/semana-atual")
    public ResponseEntity<List<RelatorioResponseDTO>> listarRelatoriosDaSemana() {
        return ResponseEntity.ok(service.listarRelatoriosUltimosSeteDias());
    }

    /**
     * Listagem paginada. Exemplos:
     *   GET /relatorios?page=0&size=20
     *   GET /relatorios?page=1&size=50&sort=dataReuniao,desc
     */
    @GetMapping
    public ResponseEntity<Page<RelatorioResponseDTO>> listarTodos(
            @PageableDefault(size = 20, sort = "dataReuniao") Pageable pageable) {
        return ResponseEntity.ok(service.listarTodosComoDTO(pageable));
    }

    @GetMapping("/celulas/{id}")
    @PreAuthorize("hasAnyRole('LIDER_CELULA', 'ADMIN', 'SECRETARIO')")
    public ResponseEntity<List<RelatorioResponseDTO>> listarPorCelula(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorCelula(id));
    }

    @GetMapping("/historico")
    public List<RelatorioResponseDTO> listarHistorico(Authentication authentication) {
        return service.listarHistoricoDaMinhaCelula(authentication.getName());
    }

    @GetMapping("/todos-relatorios")
    public ResponseEntity<List<RelatorioDiscipuladoDTO>> buscarTodos() {
        return ResponseEntity.ok(service.listarTodosOsRelatorios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── Resumo / semana ──────────────────────────────────────────────────────

    @GetMapping("/semana")
    @PreAuthorize("hasAnyRole('ADMIN', 'PASTOR', 'SECRETARIO')")
    public ResponseEntity<?> buscarResumoSemana(
            @RequestParam("inicio") String inicioStr,
            @RequestParam("fim") String fimStr) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim    = LocalDate.parse(fimStr);
            return ResponseEntity.ok(service.buscarResumoSemana(inicio, fim));
        } catch (DateTimeParseException e) {
            log.warn("Formato de data inválido — inicio: '{}', fim: '{}'", inicioStr, fimStr);
            return ResponseEntity.badRequest()
                    .body("Formato de data inválido. Use YYYY-MM-DD.");
        }
    }

    // ── Editar ───────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIDER_CELULA')")
    public ResponseEntity<String> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RelatorioRequestDTO dto) {
        try {
            service.atualizarRelatorio(id, dto);
            return ResponseEntity.ok("Relatório atualizado com sucesso!");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao atualizar relatório {}", id, e);
            return ResponseEntity.badRequest().body("Erro ao atualizar relatório: " + e.getMessage());
        }
    }

    // ── Não realizada ────────────────────────────────────────────────────────

    @PostMapping("/nao-realizada")
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<RelatorioNaoRealizadaResponse> registrarNaoRealizada(
            @RequestBody @Valid RelatorioNaoRealizadaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registrarNaoRealizada(request, userDetails.getUsername()));
    }

    // ── PDF ──────────────────────────────────────────────────────────────────

    /**
     * PDF semanal — recebe a data e busca a semana correspondente.
     * GET /relatorios/pdf-semanal?data=2024-06-10
     */
    @GetMapping("/pdf-semanal")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'PASTOR')")
    public ResponseEntity<byte[]> baixarPdfSemanal(@RequestParam String data) {
        List<RelatorioResponseDTO> relatorios = service.buscarPorSemana(data);
        byte[] pdf = pdfService.gerarPdf(relatorios);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_celulas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /**
     * PDF geral paginado — gera apenas a página atual, não todos os registros.
     * GET /relatorios/pdf?page=0&size=100
     */
    @GetMapping("/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'PASTOR')")
    public ResponseEntity<byte[]> baixarPdfGeral(
            @PageableDefault(size = 100) Pageable pageable) {
        List<RelatorioResponseDTO> relatorios = service.listarTodosComoDTO(pageable).getContent();
        byte[] pdf = pdfService.gerarPdf(relatorios);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorios.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}