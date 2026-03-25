package com.gestaoigrejaemcelula.demo.web.controller;


import com.gestaoigrejaemcelula.demo.aplication.service.EventoService;
import com.gestaoigrejaemcelula.demo.domain.entity.Evento;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEvento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@CrossOrigin(origins = "http://localhost:5173")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Evento> criar(@RequestBody Evento evento) {
        return ResponseEntity.ok(service.criar(evento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizar(
            @PathVariable Long id,
            @RequestBody Evento evento) {

        return ResponseEntity.ok(service.atualizar(id, evento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Evento>> listarPorTipo(@PathVariable TipoEvento tipo) {
        return ResponseEntity.ok(service.listarPorTipo(tipo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
