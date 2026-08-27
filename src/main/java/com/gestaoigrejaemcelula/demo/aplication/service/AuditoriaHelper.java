package com.gestaoigrejaemcelula.demo.aplication.service;


import com.gestaoigrejaemcelula.demo.aplication.dto.EventoAuditoria;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditoriaHelper {

    private final AuditoriaService auditoriaService;

    public void registrar(String entidade, String id, String nome,
                          String acao, Map<String, Object> diff) {
        String nomeUsuario = resolverUsuario();
        auditoriaService.registrar(new EventoAuditoria(
                entidade, id, nome, acao, diff,
                nomeUsuario, nomeUsuario,
                null, null, null
        ));
    }

    public void registrar(String entidade, Long id, String nome,
                          String acao, Map<String, Object> diff) {
        registrar(entidade, id != null ? String.valueOf(id) : null, nome, acao, diff);
    }

    public void registrarComAprovador(String entidade, String id, String nome,
                                      String acao, String aprovadorNome, String aprovadorEmail) {
        String nomeUsuario = resolverUsuario();
        auditoriaService.registrar(new EventoAuditoria(
                entidade, id, nome, acao, null,
                nomeUsuario, nomeUsuario,
                aprovadorNome, aprovadorEmail, null
        ));
    }

    public void registrarComAprovador(String entidade, Long id, String nome,
                                      String acao, String aprovadorNome, String aprovadorEmail) {
        registrarComAprovador(entidade, id != null ? String.valueOf(id) : null, nome, acao, aprovadorNome, aprovadorEmail);
    }

    private String resolverUsuario() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception ignored) {
            // Segurança pode não estar disponível (ex.: em contexto de schedulers ou testes)
        }
        return "sistema";
    }
}