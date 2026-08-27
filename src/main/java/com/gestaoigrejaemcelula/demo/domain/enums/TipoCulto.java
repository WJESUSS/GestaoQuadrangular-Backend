package com.gestaoigrejaemcelula.demo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoCulto {

    VITORIA("Vitória"),
    SANTA_CEIA("Santa Ceia"),
    CELEBRACAO("Celebração"),
    MISSOES("Missões");

    private final String descricao;

    TipoCulto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static TipoCulto fromString(String value) {
        for (TipoCulto tipo : TipoCulto.values()) {
            if (tipo.name().equalsIgnoreCase(value)
                    || tipo.descricao.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("TipoCulto inválido: " + value);
    }
}
