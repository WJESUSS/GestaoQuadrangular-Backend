package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoRequestDTO;
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
public class DiscipuladoRelatorioController {


    private final DiscipuladoRelatorioService service;

    /**
     * Enviar relatório semanal de discipulado
     * (líder envia)
     */
    @PostMapping("/relatorio-semanal")
    @PreAuthorize("hasAnyAuthority('LIDER_CELULA', 'SECRETARIO', 'ADMIN', 'PASTOR')")
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

    /**
     * Listar relatório da semana
     * (pastor / secretaria)
     */
    @GetMapping("/relatorio-semanal")
    @PreAuthorize("hasAnyAuthority('PASTOR', 'SECRETARIO', 'ADMIN')")
    public ResponseEntity<List<DiscipuladoRelatorio>> listarSemana(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim
    ) {
        return ResponseEntity.ok(service.listarSemana(inicio, fim));
    }
    @PostMapping("/discipulado/semana")
    public ResponseEntity<Void> salvarSemana(
            @RequestBody List<DiscipuladoRequestDTO> lista,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim
    ) {
        service.salvarRelatorioSemanal(lista, inicio, fim);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/todos-relatorios")
    public ResponseEntity<List<RelatorioDiscipuladoDTO>> buscarTodos() {
        // Retorna a lista para a secretaria
        return ResponseEntity.ok(service.listarTodosOsRelatorios());
    }
}
