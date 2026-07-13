package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;

public class AlterarDecisaoVisitanteDTO {
    private DecisaoEspiritual tipoDecisao;

    public DecisaoEspiritual getTipoDecisao() { return tipoDecisao; }
    public void setTipoDecisao(DecisaoEspiritual tipoDecisao) { this.tipoDecisao = tipoDecisao; }
}
