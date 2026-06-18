package com.gestaoigrejaemcelula.demo.aplication.dto;

/**
 * Enviado pela Secretaria para aprovar ou rejeitar uma solicitação de membro.
 */
public class DecisaoSolicitacaoMembroDTO {

    /** true = aprovar, false = rejeitar */
    private boolean aprovado;

    /** Obrigatório se aprovado = false */
    private String motivoRejeicao;

    public boolean isAprovado() { return aprovado; }
    public void setAprovado(boolean aprovado) { this.aprovado = aprovado; }

    public String getMotivoRejeicao() { return motivoRejeicao; }
    public void setMotivoRejeicao(String motivoRejeicao) { this.motivoRejeicao = motivoRejeicao; }
}