package com.gestaoigrejaemcelula.demo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoEstudoDiscipulado {

    ESTUDO_BIBLICO("Estudo bíblico"),
    ACOMPANHAMENTO("Acompanhamento"),
    VIDA_CRISTA("Vida cristã"),
    ORACAO("Oração"),
    NOVO_CONVERTIDO("Novo convertido"),
    LIDERANCA("Liderança"),
    FAMILIA("Família"),
    RELACIONAMENTO_COM_DEUS("Relacionamento com Deus"),
    OUTRO("Outro");

    private final String descricao;

    TipoEstudoDiscipulado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static TipoEstudoDiscipulado fromString(String value) {
        if (value == null) {
            return null;
        }
        for (TipoEstudoDiscipulado tipo : values()) {
            if (tipo.name().equalsIgnoreCase(value)
                    || tipo.descricao.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de estudo inválido: " + value);
    }
}
