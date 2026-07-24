package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.aplication.service.CasaDePazService;


import com.gestaoigrejaemcelula.demo.domain.enums.StatusCasaDePaz;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import jakarta.validation.Valid;
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

    public CasaDePazController(CasaDePazService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<CasaDePazResponseDTO> criar(@RequestBody @Valid CasaDePazRequestDTO dto) {
        return ResponseEntity.ok(CasaDePazResponseDTO.de(service.criar(dto)));
    }


    @GetMapping
    public ResponseEntity<List<CasaDePazResponseDTO>> listarCasas(
            @RequestParam(required = false) Long celulaId) {
        List<CasaDePazResponseDTO> lista = celulaId != null
                ? service.listarPorCelula(celulaId).stream().map(CasaDePazResponseDTO::de).toList()
                : service.listarTodas().stream().map(CasaDePazResponseDTO::de).toList();
        return ResponseEntity.ok(lista);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CasaDePazResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(CasaDePazResponseDTO.de(service.buscarPorId(id)));
    }


    @PostMapping("/{id}/visitantes/{visitanteId}")
    public ResponseEntity<CasaDePazResponseDTO> adicionarVisitante(
            @PathVariable Long id,
            @PathVariable Long visitanteId) {
        return ResponseEntity.ok(CasaDePazResponseDTO.de(service.adicionarVisitante(id, visitanteId)));
    }


    @GetMapping("/listar-visitante")
    public ResponseEntity<List<VisitanteResponseDTO>> listarVisitantes() {
        return ResponseEntity.ok(service.listar());
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<VisitanteResponseDTO>> buscar(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }


    @PostMapping("/{id}/encontros")
    public ResponseEntity<Map<String, Object>> registrarEncontro(
            @PathVariable Long id,
            @RequestBody EncontroRequestDTO dto) {
        return ResponseEntity.ok(service.registrarEncontro(id, dto));
    }


    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CasaDePazResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(CasaDePazResponseDTO.de(service.cancelar(id)));
    }


    @GetMapping("/relatorio")
    public ResponseEntity<List<RelatorioCasaDePazDTO>> relatorio(
            @RequestParam(required = false) Long celulaId,
            @RequestParam(required = false) StatusCasaDePaz status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(service.gerarRelatorio(celulaId, status, dataInicio, dataFim));
    }


    @GetMapping("/buscar-nome")
    public ResponseEntity<List<VisitanteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CasaDePazResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CasaDePazRequestDTO dto) {

        return ResponseEntity.ok(
                CasaDePazResponseDTO.de(service.atualizar(id, dto))
        );
    }


}