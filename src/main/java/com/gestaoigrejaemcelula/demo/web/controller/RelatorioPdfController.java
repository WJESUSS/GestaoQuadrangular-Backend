package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioPdfService;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioService; // Seu service de busca
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA','PASTOR')")
@RequestMapping("/api/relatorios")
public class RelatorioPdfController {

    private final RelatorioPdfService pdfService;
    private final RelatorioService relatorioService;

    public RelatorioPdfController(RelatorioPdfService pdfService, RelatorioService relatorioService) {
        this.pdfService = pdfService;
        this.relatorioService = relatorioService;
    }

    @GetMapping("/pdf-semanal")
    public ResponseEntity<byte[]> baixarPdf(@RequestParam String data) {
        // 1. Busca os dados da semana no banco
        List<RelatorioResponseDTO> relatorios = relatorioService.buscarPorSemana(data);

        // 2. Gera o PDF usando o Service que acabamos de corrigir
        byte[] pdfContents = pdfService.gerarPdf(relatorios);

        // 3. Retorna o arquivo para o navegador
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_celulas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContents);
    }
}
