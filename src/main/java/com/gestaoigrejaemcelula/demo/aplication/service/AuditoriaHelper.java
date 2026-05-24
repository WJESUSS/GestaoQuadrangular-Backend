package com.gestaoigrejaemcelula.demo.aplication.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditoriaHelper {

    private final AuditoriaService auditoriaService;

    public void registrar(String entidade, Long id, String nome,
                          String acao, Map<String, Object> diff) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String nomeUsuario  = auth != null ? auth.getName() : "sistema";
        // Se seu UserDetails customizado tiver getNome(), faça o cast aqui
        String emailUsuario = nomeUsuario; // email é o principal no Spring Security

        auditoriaService.registrar(
                entidade, id, nome, acao, diff,
                nomeUsuario, emailUsuario,
                null, null, null
        );
    }

    // Sobrecarga para aprovações/rejeições (tem aprovador explícito)
    public void registrarComAprovador(String entidade, Long id, String nome,
                                      String acao, String aprovadorNome, String aprovadorEmail) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String nomeUsuario = auth != null ? auth.getName() : "sistema";

        auditoriaService.registrar(
                entidade, id, nome, acao, null,
                nomeUsuario, nomeUsuario,
                aprovadorNome, aprovadorEmail, null
        );
    }
}