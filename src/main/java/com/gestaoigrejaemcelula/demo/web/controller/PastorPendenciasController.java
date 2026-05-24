package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.service.PastorPendenciasService;
import com.gestaoigrejaemcelula.demo.aplication.service.PastorPendenciasService.PendenciaDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.PastorPendenciasService.ResumoDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pastor")
public class PastorPendenciasController {

    private final PastorPendenciasService pendenciasService;

    public PastorPendenciasController(PastorPendenciasService pendenciasService) {
        this.pendenciasService = pendenciasService;
    }

    // =========================
    // GET /pastor/pendencias
    // Lista células com relatório e/ou discipulado pendente na semana atual
    // =========================
    @GetMapping("/pendencias")
    @PreAuthorize("hasAnyRole('PASTOR', 'ADMIN')")
    public ResponseEntity<List<PendenciaDTO>> getPendencias() {
        return ResponseEntity.ok(pendenciasService.listarPendenciasDaSemana());
    }

    // =========================
    // GET /pastor/pendencias/resumo
    // Retorna apenas os totais (útil para badges e KPIs no dashboard)
    // =========================
    @GetMapping("/pendencias/resumo")
    @PreAuthorize("hasAnyRole('PASTOR', 'ADMIN')")
    public ResponseEntity<ResumoDTO> getResumoPendencias(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semanaInicio) {

        return ResponseEntity.ok(pendenciasService.resumoPendencias(semanaInicio));
    }
}