package com.gestaoigrejaemcelula.demo.web.controller;


import com.gestaoigrejaemcelula.demo.aplication.dto.MembroRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MembroResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MembroResumoDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MembroSelectDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.MembroService;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/membros")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasAnyAuthority('ADMIN','PASTOR','LIDER_CELULA','SECRETARIA','TESOUREIRO')")

public class MembroController {

    private final MembroService service;

    public MembroController(MembroService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MembroResponseDTO> criar(
            @RequestBody MembroRequestDTO dto) {

        return ResponseEntity.ok(service.criar(dto));
    }
    @PreAuthorize("hasAnyAuthority('LIDER_CELULA')")
    @GetMapping("/sem-celula")
    public ResponseEntity<List<MembroResumoDTO>> listarSemCelula() {
        return ResponseEntity.ok(service.listarSemCelula());
    }
    @PutMapping("/{id}")
    public ResponseEntity<MembroResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody MembroRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }



    @PreAuthorize("hasAnyAuthority('ADMIN', 'PASTOR', 'LIDER_CELULA', 'TESOUREIRO')") // Adicione o TESOUREIRO aqui!
    @GetMapping
    public ResponseEntity<List<MembroResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }
    @GetMapping("/buscar")
    public ResponseEntity<List<MembroResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }
    @GetMapping("/resumo")
    public ResponseEntity<List<MembroResumoDTO>> listarResumo() {
        return ResponseEntity.ok(service.listarTodosAtivos());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MembroResponseDTO>> listarPorStatus(
            @PathVariable StatusMembro status) {

        return ResponseEntity.ok(service.listarPorStatus(status));
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
}
