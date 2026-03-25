package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.CelulaAlertaDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.AlertaCelulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas-celulas")
@RequiredArgsConstructor
public class AlertaCelulaController {

    private final AlertaCelulaService service;

    @GetMapping
    public List<CelulaAlertaDTO> listarAlertas() {
        return service.gerarAlertas();
    }
}
