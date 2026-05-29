package com.gestaoigrejaemcelula.demo.web.controller;


import com.gestaoigrejaemcelula.demo.aplication.dto.AniversarianteDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.AniversarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aniversariantes")
@CrossOrigin
public class AniversarioController {

    private final AniversarioService aniversarioService;

    public AniversarioController(AniversarioService aniversarioService) {
        this.aniversarioService = aniversarioService;
    }

    @GetMapping("/hoje")
    public List<AniversarianteDTO> listarHoje() {
        return aniversarioService.listarAniversariantesDoDia();
    }
    @GetMapping("/semana")
    public List<AniversarianteDTO> listarSemana() {
        return aniversarioService.listarAniversariantesDaSemana();
    }


    // Para LÍDER (da célula)
    @GetMapping("/celula/{celulaId}/hoje")
    public ResponseEntity<List<AniversarianteDTO>> listarHojePorCelula(@PathVariable Long celulaId) {
        return ResponseEntity.ok(aniversarioService.listarAniversariantesDoDiaPorCelula(celulaId));
    }

    @GetMapping("/celula/{celulaId}/semana")
    public ResponseEntity<List<AniversarianteDTO>> listarSemanaPorCelula(@PathVariable Long celulaId) {
        return ResponseEntity.ok(aniversarioService.listarAniversariantesSemanaPoeCelula(celulaId));
    }
}
