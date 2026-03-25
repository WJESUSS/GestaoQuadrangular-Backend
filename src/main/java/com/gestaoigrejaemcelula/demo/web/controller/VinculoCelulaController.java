package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.service.VinculoCelulaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/celulas")
@CrossOrigin(origins = "http://localhost:5173")
public class VinculoCelulaController {

    private final VinculoCelulaService service;

    public VinculoCelulaController(VinculoCelulaService service) {
        this.service = service;
    }

    // vincular membro à célula
    @PreAuthorize("hasAnyAuthority('LIDER_CELULA')")
    @PostMapping("/{celulaId}/membros/{membroId}")
    public ResponseEntity<Void> vincular(
            @PathVariable Long celulaId,
            @PathVariable Long membroId) {

        service.vincular(membroId, celulaId);
        return ResponseEntity.ok().build();
    }

    // remover membro da célula
    @PreAuthorize("hasAnyAuthority('LIDER_CELULA')")
    @DeleteMapping("/remover/membros/{membroId}")
    public ResponseEntity<Void> remover(@PathVariable Long membroId) {

        service.remover(membroId);
        return ResponseEntity.noContent().build();
    }
}
