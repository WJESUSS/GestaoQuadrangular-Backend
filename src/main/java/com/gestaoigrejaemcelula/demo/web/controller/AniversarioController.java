package com.gestaoigrejaemcelula.demo.web.controller;


import com.gestaoigrejaemcelula.demo.aplication.dto.AniversarianteDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.AniversarioService;
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
}