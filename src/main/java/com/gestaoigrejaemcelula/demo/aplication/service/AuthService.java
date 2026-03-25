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
        // 1. Autentica e já recupera o objeto de autenticação
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.senha())
        );

        // 2. Extrai o usuário (Principal) que o Spring Security carregou do banco
        // Isso assume que sua classe Usuario implementa UserDetails
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // 3. Gera o token
        String token = jwtService.gerarToken(usuario);

        return new TokenDTO(token);
    }

}
