package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.AlertaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoRelatorioResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.DiscipuladoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/discipulado") // Este mapeamento deve ser EXATAMENTE igual ao chamado no Front
@CrossOrigin(origins = "*") // Libera acesso se o Front estiver em porta diferente
public class DiscipuladoController {

    @Autowired
    private DiscipuladoService service;

    // Rota: GET /api/discipulado/alertas?mes=2026-01
    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaDTO>> getAlertas(@RequestParam("mes") String mes) {
        // O Service agora cuida de quebrar a String e converter para DTO
        List<AlertaDTO> alertas = service.buscarAlertas(mes);
        return ResponseEntity.ok(alertas);
    }

    // Rota: POST /api/discipulado/acompanhamento
    @PostMapping("/acompanhamento")
    public ResponseEntity<Void> registrarAcompanhamento(@RequestBody Map<String, Object> payload) {
        try {
            Long membroId = Long.valueOf(payload.get("membroId").toString());
            String mesRef = payload.get("mesReferencia").toString();

            service.registrarCuidado(membroId, mesRef);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // No DiscipuladoRelatorioController.java
    @GetMapping("/todos-relatorios-secretaria") // Mudei o nome aqui
    public ResponseEntity<List<DiscipuladoRelatorioResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(service.listarTodosParaSecretaria());
    }

}