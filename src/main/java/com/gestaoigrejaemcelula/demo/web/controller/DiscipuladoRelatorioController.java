package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoHistoricoItemDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoSemanaDetalheDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioDiscipuladoDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.DiscipuladoRelatorioService;
import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoRelatorio;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/discipulado")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LIDER_CELULA', 'SECRETARIO', 'ADMIN', 'PASTOR')")
public class DiscipuladoRelatorioController {

    private final DiscipuladoRelatorioService service;

    // Enviar relatório semanal
    @PostMapping("/relatorio-semanal")
    public ResponseEntity<Void> enviarRelatorioSemanal(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim,

            @RequestBody List<DiscipuladoRequestDTO> lista
    ) {
        service.salvarRelatorioSemanal(lista, inicio, fim);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Listar relatório da semana

    @GetMapping("/relatorio-semanal")
    public ResponseEntity<List<RelatorioDiscipuladoDTO>> listarSemana(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return ResponseEntity.ok(service.listarSemana(inicio, fim));
    }

    // Alternativo (corrigido o path)
    @PostMapping("/semana")
    public ResponseEntity<Void> salvarSemana(
            @RequestBody List<DiscipuladoRequestDTO> lista,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim
    ) {
        service.salvarRelatorioSemanal(lista, inicio, fim);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RelatorioDiscipuladoDTO> atualizarUnico(
            @PathVariable Long id,
            @RequestBody DiscipuladoRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizarRelatorio(id, dto));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<DiscipuladoHistoricoItemDTO>> listarHistorico() {
        return ResponseEntity.ok(service.listarHistorico());
    }
    @PutMapping("/relatorio-semanal/{id}")
    public ResponseEntity<Void> atualizarRelatorioSemanal(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestBody List<DiscipuladoRequestDTO> lista
    ) {
        service.atualizarRelatorioSemanal(id, lista, inicio, fim);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/relatorio-semanal/{id}")
    public ResponseEntity<DiscipuladoSemanaDetalheDTO> buscarDetalhe(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.buscarDetalhe(id));
    }

}