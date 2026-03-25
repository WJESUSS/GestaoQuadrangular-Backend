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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARIO', 'LIDER_CELULA')")
public class RelatorioController {


    private final RelatorioService service;
    private final RelatorioPdfService pdfService;

    public RelatorioController(RelatorioService relatorioService,
                               RelatorioPdfService pdfService) {
        this.service = relatorioService;
        this.pdfService = pdfService;
    }



    @PostMapping
    @PreAuthorize("hasAuthority('LIDER_CELULA')")
    public ResponseEntity<String> criar(@RequestBody @Valid RelatorioRequestDTO dto) {
        try {
            service.salvarRelatorio(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Relatório criado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao criar relatório: " + e.getMessage());
        }
    }



    @GetMapping("/semana-atual")
    public ResponseEntity<List<RelatorioResponseDTO>> listarRelatoriosDaSemana() {
        // Chamamos o service para garantir que a conversão para DTO aconteça
        List<RelatorioResponseDTO> dtos = service.listarRelatoriosUltimosSeteDias();
        return ResponseEntity.ok(dtos);
    }
    @GetMapping
    public ResponseEntity<List<RelatorioResponseDTO>> listarTodos() {
        // Nunca retorne a Entity bruta se ela tiver relacionamentos Lazy
        List<RelatorioResponseDTO> dtos = service.listarTodosComoDTO();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyAuthority('LIDER_CELULA', 'ADMIN', 'SECRETARIO')")
    @GetMapping("/celulas/{id}")
    public ResponseEntity<List<RelatorioResponseDTO>> listarPorCelula(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorCelula(id));
    }

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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'PASTOR', 'SECRETARIO')")
    @GetMapping("/semana")
    public ResponseEntity<RelatorioResumoDTO> buscarPorSemana(
            @RequestParam("inicio") String inicioStr,
            @RequestParam("fim") String fimStr) {

        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fim = LocalDate.parse(fimStr);

            // Busca no serviço usando o intervalo exato vindo do Front-end
            RelatorioResumoDTO resumo = service.buscarResumoSemana(inicio, fim);
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de data inválido. Use YYYY-MM-DD");
        }
    }
}

