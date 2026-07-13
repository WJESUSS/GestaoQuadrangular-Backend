package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.MotivoCancelamentoMissao70;

public class CancelarMissao70RequestDTO {

    private MotivoCancelamentoMissao70 motivoCancelamento;
    private String observacaoCancelamento;

    public MotivoCancelamentoMissao70 getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(MotivoCancelamentoMissao70 motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }
    public String getObservacaoCancelamento() { return observacaoCancelamento; }
    public void setObservacaoCancelamento(String observacaoCancelamento) { this.observacaoCancelamento = observacaoCancelamento; }
}