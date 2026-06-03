package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Body do POST /visitantes/{id}/jornada
 */
public class RegistrarDecisaoRequest {

    @NotNull(message = "A decisão é obrigatória")
    private DecisaoEspiritual decisao;

    /** Se não informado, usa LocalDate.now() */
    private LocalDate dataRegistro;

    /** Contexto livre: "Culto domingo", "Casa de Paz Flores" */
    private String observacao;

    public DecisaoEspiritual getDecisao() { return decisao; }
    public void setDecisao(DecisaoEspiritual decisao) { this.decisao = decisao; }

    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}