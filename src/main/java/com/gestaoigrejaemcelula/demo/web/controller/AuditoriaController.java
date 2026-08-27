package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.AuditoriaDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService service;

    /**
     * GET /auditoria
     * Filtros opcionais: entidade, acao, usuario, entidadeId, de, ate, page, size
     * Protegido: apenas ADMIN e PASTOR
     */
    @GetMapping
    public ResponseEntity<Page<AuditoriaDTO>> listar(
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String entidadeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime de,  // ✅
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime ate, // ✅
            @RequestParam(defaultValue = "0") int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(
                service.listar(entidade, acao, usuario, entidadeId, de, ate, page, size)
        );
    }


    /**
     * GET /auditoria/{entidade}/{id}
     * Histórico de um registro específico (ex: /auditoria/MEMBRO/42)
     */
    @GetMapping("/{entidade}/{id}")

    public ResponseEntity<Page<AuditoriaDTO>> historico(
            @PathVariable String entidade,
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(
                service.historicoPorRegistro(entidade, id, page, size)
        );
    }
}
