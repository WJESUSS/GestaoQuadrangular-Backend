package com.gestaoigrejaemcelula.demo.domain.enums;

public enum StatusSolicitacaoMembro {

    PENDENTE("Pendente"),
    APROVADO("Aprovado"),
    REJEITADO("Rejeitado");

    private final String descricao;

    StatusSolicitacaoMembro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}