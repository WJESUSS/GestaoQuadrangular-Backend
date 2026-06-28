package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.aplication.service.MembroService;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/membros")


public class MembroController {

    private final MembroService service;
    private final MembroRepository membroRepository;

    public MembroController(MembroService service, MembroRepository membroRepository) {
        this.service = service;
        this.membroRepository = membroRepository;
    }

    @PostMapping
    public ResponseEntity<MembroResponseDTO> criar(@RequestBody @Valid MembroRequestDTO dto) {
        return ResponseEntity.ok(service.criar(dto));
    }


    @GetMapping("/sem-celula")
    public ResponseEntity<Page<MembroResumoDTO>> listarSemCelula(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.listarSemCelula(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembroResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MembroRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public Page<MembroResponseDTO> listar(
            @PageableDefault(size = 20) Pageable pageable) {
        return service.listarTodos(pageable);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<MembroResponseDTO>> buscarPorNome(
            @RequestParam String nome,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.buscarPorNome(nome, pageable));
    }

    @GetMapping("/resumo")
    public ResponseEntity<Page<MembroResumoDTO>> listarResumo(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.listarTodosAtivos(pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<MembroResponseDTO>> listarPorStatus(
            @PathVariable StatusMembro status,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(service.listarPorStatus(status, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/select")
    public ResponseEntity<List<MembroSelectDTO>> listarParaSelect() {
        return ResponseEntity.ok(service.listarParaSelect());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(
            @PathVariable Long id,
            @RequestParam StatusMembro status,
            @RequestParam(required = false) String observacao) {

        service.alterarStatus(id, status, observacao);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/celula/{celulaId}/aniversariantes-hoje")
    public ResponseEntity<List<CelulaResponseDTO.MembroDTO>> aniversariantesHoje(@PathVariable Long celulaId) {
        List<CelulaResponseDTO.MembroDTO> lista = service.buscarAniversariantesHoje(celulaId);
        return ResponseEntity.ok(lista);
    }

}