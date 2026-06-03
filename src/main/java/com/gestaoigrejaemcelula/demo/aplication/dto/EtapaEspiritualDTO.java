package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.JornadaEspiritual;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import java.time.LocalDate;

/**
 * Representa uma etapa da jornada espiritual no response.
 */
public class EtapaEspiritualDTO {

    private DecisaoEspiritual decisao;
    private LocalDate dataRegistro;
    private String registradoPor;
    private String observacao;

    public EtapaEspiritualDTO() {}

    public EtapaEspiritualDTO(JornadaEspiritual j) {
        this.decisao       = j.getDecisao();
        this.dataRegistro  = j.getDataRegistro();
        this.registradoPor = j.getRegistradoPor();
        this.observacao    = j.getObservacao();
    }

    public DecisaoEspiritual getDecisao() { return decisao; }
    public void setDecisao(DecisaoEspiritual decisao) { this.decisao = decisao; }

    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }

    public String getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(String registradoPor) { this.registradoPor = registradoPor; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}