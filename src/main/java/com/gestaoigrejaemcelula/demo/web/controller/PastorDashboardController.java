package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.PastorMetricasDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.PastorDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pastor")
@CrossOrigin("*")
public class PastorDashboardController {

    @Autowired
    private PastorDashboardService service;

    @GetMapping("/metricas")
    public ResponseEntity<PastorMetricasDTO> metricas(
            @RequestParam("mes") String mes) {

        return ResponseEntity.ok(service.carregarMetricas(mes));
    }
}

