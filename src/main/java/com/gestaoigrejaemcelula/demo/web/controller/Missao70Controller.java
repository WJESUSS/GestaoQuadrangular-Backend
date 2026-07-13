package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.aplication.service.Missao70Service;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
     * Lista todas ou filtra por célula e/ou status.
     * Uso: /api/missao70?celulaId=2&status=EM_ANDAMENTO&page=0&size=20&sort=id,desc
     */
    @GetMapping
    public ResponseEntity<Page<Missao70ResponseDTO>> listar(
            @RequestParam(required = false) Long celulaId,
            @RequestParam(required = false) StatusMissao70 status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Missao70ResponseDTO> pagina;
        if (celulaId != null && status != null) {
            pagina = service.listarPorCelulaEStatusPaginado(celulaId, status, pageable);
        } else if (celulaId != null) {
            pagina = service.listarPorCelulaPaginado(celulaId, pageable);
        } else if (status != null) {
            pagina = service.listarPorStatusPaginado(status, pageable);
        } else {
            pagina = service.listarTodasPaginado(pageable);
        }
        return ResponseEntity.ok(pagina);
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
     * Body: { dataEncontro, observacoes, visitantesPresentesIds[], decisoes[] }
     */
    @PostMapping("/{id}/encontros")
    public ResponseEntity<Map<String, Object>> registrarEncontro(
            @PathVariable Long id,
            @RequestBody EncontroMissao70RequestDTO dto) {
        return ResponseEntity.ok(service.registrarEncontro(id, dto));
    }

    /**
     * NOVO
     * GET /api/missao70/{id}/encontros
     * Lista o histórico de cultos já registrados nesta casa
     */
    @GetMapping("/{id}/encontros")
    public ResponseEntity<List<EncontroMissao70ResponseDTO>> listarEncontros(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarEncontros(id));
    }

    /**
     * NOVO
     * PUT /api/missao70/{id}/encontros/{encontroId}
     * Edita um culto já registrado (data, observações, presença)
     */
    @PutMapping("/{id}/encontros/{encontroId}")
    public ResponseEntity<EncontroMissao70ResponseDTO> atualizarEncontro(
            @PathVariable Long id,
            @PathVariable Long encontroId,
            @RequestBody EncontroMissao70RequestDTO dto) {
        return ResponseEntity.ok(EncontroMissao70ResponseDTO.de(service.atualizarEncontro(id, encontroId, dto)));
    }

    /**
     * PATCH /api/missao70/{id}/cancelar
     * Cancela a Missão 70
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Missao70ResponseDTO> cancelar(
            @PathVariable Long id,
            @RequestBody CancelarMissao70RequestDTO dto) {
        Missao70ResponseDTO response = service.cancelar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/missao70/{id}/visitantes/{visitanteId}/decisao
     * Altera a decisão espiritual de um visitante sem registrar um culto.
     */
    @PatchMapping("/{id}/visitantes/{visitanteId}/decisao")
    public ResponseEntity<Void> alterarDecisaoVisitante(
            @PathVariable Long id,
            @PathVariable Long visitanteId,
            @RequestBody AlterarDecisaoVisitanteDTO dto) {
        service.alterarDecisaoVisitante(id, visitanteId, dto);
        return ResponseEntity.noContent().build();
    }
}