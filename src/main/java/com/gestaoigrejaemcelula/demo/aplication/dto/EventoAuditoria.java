package com.gestaoigrejaemcelula.demo.aplication.dto;

import java.util.Map;

public record EventoAuditoria(
        String entidade,
        Long entidadeId,
        String entidadeNome,
        String acao,
        Map<String, Object> detalhes,
        String usuarioNome,
        String usuarioEmail,
        String aprovadorNome,
        String aprovadorEmail,
        String ipOrigem
) {}
