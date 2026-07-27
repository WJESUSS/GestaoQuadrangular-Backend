package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.LoginDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.TokenDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import com.gestaoigrejaemcelula.demo.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public TokenDTO login(LoginDTO dto) {

        String email = dto.email().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (!usuario.isEnabled()) {
            throw new DisabledException("Conta desativada");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, dto.senha())
        );

        usuario.setUltimoAcesso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario);

        return new TokenDTO(token);
    }
}
