package com.gestaoigrejaemcelula.demo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusAcompanhamentoDiscipulado {

    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusAcompanhamentoDiscipulado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static StatusAcompanhamentoDiscipulado fromString(String value) {
        if (value == null) {
            return null;
        }
        for (StatusAcompanhamentoDiscipulado status : values()) {
            if (status.name().equalsIgnoreCase(value)
                    || status.descricao.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + value);
    }
}
