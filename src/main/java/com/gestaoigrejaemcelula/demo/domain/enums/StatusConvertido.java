package com.gestaoigrejaemcelula.demo.domain.enums;

public enum StatusConvertido {

    AGUARDANDO_BATISMO("Aguardando Batismo"),
    MEMBRO("Membro");

    private final String descricao;

    StatusConvertido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
