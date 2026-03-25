package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioEncontroResumoDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioEncontroService;
import com.gestaoigrejaemcelula.demo.domain.entity.FichaEncontro;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/relatorios/encontro")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA','PASTOR')")
public class RelatorioEncontroController {

    private final RelatorioEncontroService service;

    public RelatorioEncontroController(RelatorioEncontroService service) {
        this.service = service;
    }

    @GetMapping("/resumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA','PASTOR')")
    public ResponseEntity<RelatorioEncontroResumoDTO> resumo() {
        return ResponseEntity.ok(service.gerarResumo());
    }
    @GetMapping("/periodo")
    public ResponseEntity<List<FichaEncontro>> buscarPorPeriodo(
            @RequestParam(value = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(value = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        // Lógica de segurança: se as datas vierem nulas, define um padrão (ex: últimos 30 dias)
        LocalDate dataInicioBusca = (inicio != null) ? inicio : LocalDate.now().minusDays(30);
        LocalDate dataFimBusca = (fim != null) ? fim : LocalDate.now();

        List<FichaEncontro> fichas = service.buscarPorPeriodo(dataInicioBusca, dataFimBusca);
        return ResponseEntity.ok(fichas);
    }
}
