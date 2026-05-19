package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.EncontroMissao70RequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.Missao70RequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.Missao70ResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.Missao70Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/missao70")
public class Missao70Controller {

    private final Missao70Service service;

    public Missao70Controller(Missao70Service service) {
        this.service = service;
    }

    /**
     * POST /api/missao70
     * Cria uma nova Missão 70
     */
    @PostMapping
    public ResponseEntity<Missao70ResponseDTO> criar(@RequestBody Missao70RequestDTO dto) {
        return ResponseEntity.ok(Missao70ResponseDTO.de(service.criar(dto)));
    }

    /**
     * GET /api/missao70
     * Lista todas ou filtra por célula: /api/missao70?celulaId=2
     */
    @GetMapping
    public ResponseEntity<List<Missao70ResponseDTO>> listar(
            @RequestParam(required = false) Long celulaId) {
        List<Missao70ResponseDTO> lista = celulaId != null
                ? service.listarPorCelula(celulaId).stream().map(Missao70ResponseDTO::de).toList()
                : service.listarTodas().stream().map(Missao70ResponseDTO::de).toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * GET /api/missao70/{id}
     * Busca uma Missão 70 por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Missao70ResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(Missao70ResponseDTO.de(service.buscarPorId(id)));
    }

    /**
     * PUT /api/missao70/{id}
     * Atualiza dados cadastrais da Missão 70
     */
    @PutMapping("/{id}")
    public ResponseEntity<Missao70ResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody Missao70RequestDTO dto) {
        return ResponseEntity.ok(Missao70ResponseDTO.de(service.atualizar(id, dto)));
    }

    /**
     * POST /api/missao70/{id}/visitantes/{visitanteId}
     * Adiciona um visitante à Missão 70
     */
    @PostMapping("/{id}/visitantes/{visitanteId}")
    public ResponseEntity<Missao70ResponseDTO> adicionarVisitante(
            @PathVariable Long id,
            @PathVariable Long visitanteId) {
        return ResponseEntity.ok(Missao70ResponseDTO.de(service.adicionarVisitante(id, visitanteId)));
    }

    /**
     * POST /api/missao70/{id}/encontros
     * Registra o encontro de uma semana (1 a 4)
     * Body: { dataEncontro, numeroSemana, observacoes, decisoes[] }
     */
    @PostMapping("/{id}/encontros")
    public ResponseEntity<Map<String, Object>> registrarEncontro(
            @PathVariable Long id,
            @RequestBody EncontroMissao70RequestDTO dto) {
        return ResponseEntity.ok(service.registrarEncontro(id, dto));
    }

    /**
     * PATCH /api/missao70/{id}/cancelar
     * Cancela a Missão 70
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Missao70ResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(Missao70ResponseDTO.de(service.cancelar(id)));
    }
}