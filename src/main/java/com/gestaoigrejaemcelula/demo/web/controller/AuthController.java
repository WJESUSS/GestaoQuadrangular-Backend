package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.LoginDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.TokenDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO dto) {

        // 🔥 normaliza email (resolve seu erro de login)
        String emailNormalizado = dto.email().trim().toLowerCase();

        // cria novo DTO com email corrigido
        LoginDTO novoDto = new LoginDTO(emailNormalizado, dto.senha());

        var token = authService.login(novoDto);

        return ResponseEntity.ok(token);
    }
}