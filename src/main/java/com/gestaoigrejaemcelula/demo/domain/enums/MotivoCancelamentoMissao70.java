package com.gestaoigrejaemcelula.demo.domain.enums;



public enum MotivoCancelamentoMissao70 {

    ANFITRIAO_DESISTIU("Anfitrião desistiu"),
    SEM_INTERESSE_MORADORES("Falta de interesse dos moradores"),
    MUDANCA_ENDERECO("Anfitrião mudou de endereço"),
    FALTA_DISPONIBILIDADE_LIDER("Falta de disponibilidade do líder/auxiliar"),
    BAIXA_FREQUENCIA_VISITANTES("Baixa frequência de visitantes"),
    CONFLITO_HORARIO("Conflito de horário"),
    PROBLEMA_SEGURANCA_LOCAL("Problema de segurança no local"),
    OUTRO("Outro motivo");

    private final String descricao;

    MotivoCancelamentoMissao70(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
