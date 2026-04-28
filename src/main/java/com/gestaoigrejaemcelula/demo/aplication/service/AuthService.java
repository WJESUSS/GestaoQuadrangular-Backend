package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.LoginDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.TokenDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import com.gestaoigrejaemcelula.demo.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public TokenDTO login(LoginDTO dto) {

        // ✅ NORMALIZAÇÃO COMPLETA (ESSENCIAL)
        String email = dto.email().trim().toLowerCase();

        // 1. Autentica
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        dto.senha()
                )
        );

        // 2. Busca usuário
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(dto.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // 3. Gera token
        String token = jwtService.gerarToken(usuario);

        return new TokenDTO(token);
    }
}