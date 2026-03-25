package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.VisitanteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/visitantes")
@CrossOrigin(origins = "http://localhost:5173")
// 💡 Mudamos para hasAnyAuthority e usamos os nomes EXATOS do seu banco (SECRETARIO masculino)
@PreAuthorize("hasAnyAuthority('ADMIN', 'SECRETARIO', 'LIDER_CELULA', 'PASTOR')")
public class VisitanteController {

    private final VisitanteService service;

    public VisitanteController(VisitanteService service) {
        this.service = service;
    }

    @PostMapping
    // 💡 Removi o hasRole que estava travando o acesso
    public ResponseEntity<VisitanteResponseDTO> cadastrar(@RequestBody VisitanteRequestDTO dto) {
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
            @RequestBody VisitanteRequestDTO dto) {

        return ResponseEntity.ok(service.atualizar(id, dto));
    }


    @GetMapping("/celula/{celulaId}/ativos")
    public List<VisitanteResponseDTO> listarAtivosPorCelula(
            @PathVariable Long celulaId) {

        return service.listarAtivosPorCelula(celulaId);
    }

}