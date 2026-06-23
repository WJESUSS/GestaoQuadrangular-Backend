package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.DecisaoSolicitacaoMembroDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.SolicitacaoMembroFichaRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.SolicitacaoMembroFichaResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.SolicitacaoMembroFichaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitacoes-ficha")
public class SolicitacaoMembroFichaController {

    private final SolicitacaoMembroFichaService service;

    public SolicitacaoMembroFichaController(SolicitacaoMembroFichaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SolicitacaoMembroFichaResponseDTO> enviarFicha(
            @RequestBody @Valid SolicitacaoMembroFichaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviarFicha(dto));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<SolicitacaoMembroFichaResponseDTO>> listarMinhas() {
        return ResponseEntity.ok(service.listarMinhasSolicitacoes());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<Page<SolicitacaoMembroFichaResponseDTO>> listarPendentes(Pageable pageable) {
        return ResponseEntity.ok(service.listarPendentes(pageable));
    }

    @GetMapping
    public ResponseEntity<Page<SolicitacaoMembroFichaResponseDTO>> listarTodas(Pageable pageable) {
        return ResponseEntity.ok(service.listarTodas(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoMembroFichaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/decidir")
    public ResponseEntity<SolicitacaoMembroFichaResponseDTO> decidir(
            @PathVariable Long id,
            @RequestBody @Valid DecisaoSolicitacaoMembroDTO decisao) {
        return ResponseEntity.ok(service.decidir(id, decisao));
    }
}
