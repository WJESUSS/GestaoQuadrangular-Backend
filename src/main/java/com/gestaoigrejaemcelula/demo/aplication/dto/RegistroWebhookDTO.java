package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.RegistroWebhook;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegistroWebhookDTO {
    private final Long id;
    private final String tipoEvento;
    private final String numeroDestino;
    private final String status;
    private final String idMensagem;
    private final String payload;
    private final String textoMensagem;   // ← novo
    private final String tipoMensagem;   // ← novo
    private final LocalDateTime recebidoEm;

    public RegistroWebhookDTO(RegistroWebhook r) {
        this.id             = r.getId();
        this.tipoEvento     = r.getTipoEvento();
        this.numeroDestino  = r.getNumeroDestino();
        this.status         = r.getStatus();
        this.idMensagem     = r.getIdMensagem();
        this.payload        = r.getPayload();
        this.textoMensagem  = r.getTextoMensagem();
        this.tipoMensagem   = r.getTipoMensagem();
        this.recebidoEm     = r.getRecebidoEm();
    }
}