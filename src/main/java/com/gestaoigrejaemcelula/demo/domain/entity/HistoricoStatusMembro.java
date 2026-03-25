package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HistoricoStatusMembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Membro membro;

    @Enumerated(EnumType.STRING)
    private StatusMembro statusAnterior;

    @Enumerated(EnumType.STRING)
    private StatusMembro statusNovo;

    private LocalDateTime dataAlteracao;

    private String observacao;

    // getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Membro getMembro() {
        return membro;
    }

    public void setMembro(Membro membro) {
        this.membro = membro;
    }

    public StatusMembro getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(StatusMembro statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public StatusMembro getStatusNovo() {
        return statusNovo;
    }

    public void setStatusNovo(StatusMembro statusNovo) {
        this.statusNovo = statusNovo;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}