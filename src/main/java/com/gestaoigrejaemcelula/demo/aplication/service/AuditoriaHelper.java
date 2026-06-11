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
        String nomeUsuario = resolverUsuario();
        auditoriaService.registrar(
                entidade, id, nome, acao, diff,
                nomeUsuario, nomeUsuario,
                null, null, null
        );
    }

    public void registrarComAprovador(String entidade, Long id, String nome,
                                      String acao, String aprovadorNome, String aprovadorEmail) {
        String nomeUsuario = resolverUsuario();
        auditoriaService.registrar(
                entidade, id, nome, acao, null,
                nomeUsuario, nomeUsuario,
                aprovadorNome, aprovadorEmail, null
        );
    }

    private String resolverUsuario() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception ignored) {}
        return "sistema";
    }
}