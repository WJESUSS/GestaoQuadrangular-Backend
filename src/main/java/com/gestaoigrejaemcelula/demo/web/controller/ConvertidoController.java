package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.AutorizarMembresiaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.ConvertidoRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.ConvertidoResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.ConvertidoService;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusConvertido;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/convertidos")
public class ConvertidoController {

    private final ConvertidoService service;

    public ConvertidoController(ConvertidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConvertidoResponseDTO> registrar(
            @RequestBody @Valid ConvertidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ConvertidoResponseDTO>> listar(
            @RequestParam(required = false) StatusConvertido status,
            Pageable pageable) {
        if (status != null) {
            return ResponseEntity.ok(service.listarPorStatus(status, pageable));
        }
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConvertidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/autorizar-membresia")
    public ResponseEntity<ConvertidoResponseDTO> autorizarMembresia(
            @PathVariable Long id,
            @RequestBody @Valid AutorizarMembresiaDTO dto) {
        return ResponseEntity.ok(service.autorizarMembresia(id, dto));
    }
}
