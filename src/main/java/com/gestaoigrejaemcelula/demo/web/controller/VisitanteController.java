package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.VisitanteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/visitantes")
// 💡 Mudamos para hasAnyAuthority e usamos os nomes EXATOS do seu banco (SECRETARIO masculino)
@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA', 'PASTOR')")
public class VisitanteController {

    private final VisitanteService service;

    public VisitanteController(VisitanteService service) {
        this.service = service;
    }

    @PostMapping
    // 💡 Removi o hasRole que estava travando o acesso
    public ResponseEntity<VisitanteResponseDTO> cadastrar(@RequestBody @Valid VisitanteRequestDTO dto) {
        return ResponseEntity.ok(service.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<VisitanteResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<VisitanteResponseDTO>> buscar(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARIO', 'LIDER_CELULA','PASTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<VisitanteResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid VisitanteRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<VisitanteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/celula/{celulaId}/ativos")
    public List<VisitanteResponseDTO> listarAtivosPorCelula(
            @PathVariable Long celulaId) {

        return service.listarAtivosPorCelula(celulaId);
    }
    @GetMapping("/{id}/historico-decisoes")
    public ResponseEntity<?> historicoDecisoes(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarDecisaoAtual(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
    @PatchMapping("/{id}/arquivar")
    public ResponseEntity<Void> arquivar(@PathVariable Long id) {
        service.arquivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desarquivar")
    public ResponseEntity<Void> desarquivar(@PathVariable Long id) {
        service.desarquivar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/arquivados")
    public ResponseEntity<List<VisitanteResponseDTO>> listarArquivados() {
        return ResponseEntity.ok(service.listarArquivados());
    }
}