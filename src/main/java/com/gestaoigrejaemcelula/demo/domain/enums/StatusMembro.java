package com.gestaoigrejaemcelula.demo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusMembro {

    ATIVO("Ativo"),
    AFASTADO("Afastado"),
    TRANSFERIDO("Transferido"),
    FALECIDO("Falecido"),
    INATIVO("Inativo");

    private final String descricao;

    StatusMembro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean deveRemoverVinculos() {
        return this != ATIVO;
    }

    @JsonCreator
    public static StatusMembro fromString(String value) {
        for (StatusMembro status : StatusMembro.values()) {
            if (status.name().equalsIgnoreCase(value)
                    || status.descricao.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("StatusMembro inválido: " + value);
    }
}