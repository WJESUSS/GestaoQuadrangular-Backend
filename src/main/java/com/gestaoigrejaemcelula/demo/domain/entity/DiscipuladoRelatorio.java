package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "discipulado_relatorio")
public class DiscipuladoRelatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne // Verifique se esta anotação existe
    @JoinColumn(name = "celula_id")
    private Celula celula;

    private LocalDate semanaInicio;
    private LocalDate semanaFim;

    @ManyToOne
    private Membro membro;

    private boolean escolaBiblica;
    private boolean cultoSemana; // quarta ou quinta
    private boolean domingoManha;
    private boolean domingoNoite;

    public Celula getCelula() {
        return celula;
    }

    public void setCelula(Celula celula) {
        this.celula = celula;
    }

    private int totalPresencas; // calculado

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSemanaInicio() {
        return semanaInicio;
    }

    public void setSemanaInicio(LocalDate semanaInicio) {
        this.semanaInicio = semanaInicio;
    }

    public LocalDate getSemanaFim() {
        return semanaFim;
    }

    public void setSemanaFim(LocalDate semanaFim) {
        this.semanaFim = semanaFim;
    }

    public Membro getMembro() {
        return membro;
    }

    public void setMembro(Membro membro) {
        this.membro = membro;
    }

    public boolean isEscolaBiblica() {
        return escolaBiblica;
    }

    public void setEscolaBiblica(boolean escolaBiblica) {
        this.escolaBiblica = escolaBiblica;
    }

    public boolean isCultoSemana() {
        return cultoSemana;
    }

    public void setCultoSemana(boolean cultoSemana) {
        this.cultoSemana = cultoSemana;
    }

    public boolean isDomingoManha() {
        return domingoManha;
    }

    public void setDomingoManha(boolean domingoManha) {
        this.domingoManha = domingoManha;
    }

    public boolean isDomingoNoite() {
        return domingoNoite;
    }

    public void setDomingoNoite(boolean domingoNoite) {
        this.domingoNoite = domingoNoite;
    }

    public int getTotalPresencas() {
        return totalPresencas;
    }

    public void setTotalPresencas(int totalPresencas) {
        this.totalPresencas = totalPresencas;
    }

    public Usuario getLider() {
        return lider;
    }

    public void setLider(Usuario lider) {
        this.lider = lider;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    @ManyToOne
    private Usuario lider;

    private LocalDateTime dataEnvio;

    public void calcularPresenca() {
        int total = 0;
        if (escolaBiblica) total++;
        if (cultoSemana) total++;
        if (domingoManha) total++;
        if (domingoNoite) total++;
        this.totalPresencas = total;
    }

}
