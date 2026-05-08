package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.aplication.service.CasaDePazPdfService;
import com.gestaoigrejaemcelula.demo.aplication.service.CasaDePazService;


import com.gestaoigrejaemcelula.demo.domain.entity.CasaDePaz;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusCasaDePaz;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/casas-de-paz")
public class CasaDePazController {

    private final CasaDePazService service;
    private final CasaDePazPdfService  pdfService;

    public CasaDePazController(CasaDePazService service, CasaDePazPdfService pdfService) {
        this.service = service;
        this.pdfService = pdfService;
    }

    // POST /api/casas-de-paz
    @PostMapping
    public ResponseEntity<CasaDePazResponseDTO> criar(@RequestBody CasaDePazRequestDTO dto) {
        return ResponseEntity.ok(CasaDePazResponseDTO.de(service.criar(dto)));
    }

    // GET /api/casas-de-paz?celulaId=2   ← corrigido: era "lista-casa"
    @GetMapping
    public ResponseEntity<List<CasaDePazResponseDTO>> listarCasas(
            @RequestParam(required = false) Long celulaId) {
        List<CasaDePazResponseDTO> lista = celulaId != null
                ? service.listarPorCelula(celulaId).stream().map(CasaDePazResponseDTO::de).toList()
                : service.listarTodas().stream().map(CasaDePazResponseDTO::de).toList();
        return ResponseEntity.ok(lista);
    }

    // GET /api/casas-de-paz/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CasaDePazResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(CasaDePazResponseDTO.de(service.buscarPorId(id)));
    }

    // POST /api/casas-de-paz/{id}/visitantes/{visitanteId}
    @PostMapping("/{id}/visitantes/{visitanteId}")
    public ResponseEntity<CasaDePazResponseDTO> adicionarVisitante(
            @PathVariable Long id,
            @PathVariable Long visitanteId) {
        return ResponseEntity.ok(CasaDePazResponseDTO.de(service.adicionarVisitante(id, visitanteId)));
    }

    // GET /api/casas-de-paz/listar-visitante   ← corrigido: barra inicial adicionada
    @GetMapping("/listar-visitante")
    public ResponseEntity<List<VisitanteResponseDTO>> listarVisitantes() {
        return ResponseEntity.ok(service.listar());
    }

    // GET /api/casas-de-paz/buscar?nome=...
    @GetMapping("/buscar")
    public ResponseEntity<List<VisitanteResponseDTO>> buscar(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    // POST /api/casas-de-paz/{id}/encontros
    @PostMapping("/{id}/encontros")
    public ResponseEntity<Map<String, Object>> registrarEncontro(
            @PathVariable Long id,
            @RequestBody EncontroRequestDTO dto) {
        return ResponseEntity.ok(service.registrarEncontro(id, dto));
    }

    // PATCH /api/casas-de-paz/{id}/cancelar
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CasaDePazResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(CasaDePazResponseDTO.de(service.cancelar(id)));
    }

    // GET /api/casas-de-paz/relatorio
    @GetMapping("/relatorio")
    public ResponseEntity<List<RelatorioCasaDePazDTO>> relatorio(
            @RequestParam(required = false) Long celulaId,
            @RequestParam(required = false) StatusCasaDePaz status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(service.gerarRelatorio(celulaId, status, dataInicio, dataFim));
    }

    // GET /api/casas-de-paz/buscar-nome?nome=...
    // Nota: equivalente ao /buscar acima — considere unificar os dois em um só endpoint
    @GetMapping("/buscar-nome")
    public ResponseEntity<List<VisitanteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CasaDePazResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody CasaDePazRequestDTO dto) {

        return ResponseEntity.ok(
                CasaDePazResponseDTO.de(service.atualizar(id, dto))
        );
    }

    @PostMapping("/relatorio/pdf")
    public ResponseEntity<byte[]> gerarPdf(@RequestBody CasaDePazPdfRequestDTO request) {
        try {
            byte[] pdf = pdfService.gerar(request);

            String nomeArquivo = "Relatorio_CasasDePaz";
            if (request.getCelulaName() != null && !request.getCelulaName().isBlank()) {
                // Remove caracteres especiais para o nome do arquivo
                String celulaSafe = request.getCelulaName()
                        .replaceAll("[^a-zA-ZÀ-ú0-9 ]", "")
                        .trim()
                        .replace(" ", "_");
                nomeArquivo += "_" + celulaSafe;
            }
            nomeArquivo += "_" + LocalDate.now() + ".pdf";

            // Encode para suportar caracteres especiais no header
            String encodedName = URLEncoder.encode(nomeArquivo, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + nomeArquivo + "\"; filename*=UTF-8''" + encodedName)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(pdf.length))
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}