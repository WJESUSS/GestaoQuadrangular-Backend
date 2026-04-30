package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.AlertaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.CelulaAlertaDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.AlertaCelulaService;
import com.gestaoigrejaemcelula.demo.aplication.service.MembroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas-celulas")
@RequiredArgsConstructor
public class AlertaCelulaController {

    private final AlertaCelulaService service;
    private final MembroService membroService;

    @GetMapping
    public List<CelulaAlertaDTO> listarAlertas() {
        return service.gerarAlertas();
    }

    @GetMapping("/membros-ausentes")
    public ResponseEntity<List<AlertaDTO>> getMembrosAusentes() {
        List<AlertaDTO> alertas = membroService.obterAlertasCriticos();

        if (alertas.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(alertas);
    }
}