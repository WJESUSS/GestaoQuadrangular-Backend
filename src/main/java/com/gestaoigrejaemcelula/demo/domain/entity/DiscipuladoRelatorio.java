package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "discipulado_relatorio", indexes = {
    @Index(name = "idx_discipulado_celula_semana", columnList = "celula_id, semanaInicio, semanaFim"),
    @Index(name = "idx_discipulado_semana", columnList = "semanaInicio, semanaFim"),
    @Index(name = "idx_discipulado_membro", columnList = "membro_id")
})
public class DiscipuladoRelatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne // Verifique se esta anotação existe
    @JoinColumn(name = "celula_id")
    private Celula celula;
    @Column(name = "just_escola_biblica")
    private String justEscolaBiblica;

    @Column(name = "just_quarta_noite")
    private String justQuartaNoite;

    @Column(name = "just_quinta_noite")
    private String justQuintaNoite;

    public String getJustEscolaBiblica() {
        return justEscolaBiblica;
    }

    public void setJustEscolaBiblica(String justEscolaBiblica) {
        this.justEscolaBiblica = justEscolaBiblica;
    }

    public String getJustQuartaNoite() {
        return justQuartaNoite;
    }

    public void setJustQuartaNoite(String justQuartaNoite) {
        this.justQuartaNoite = justQuartaNoite;
    }

    public String getJustQuintaNoite() {
        return justQuintaNoite;
    }

    public void setJustQuintaNoite(String justQuintaNoite) {
        this.justQuintaNoite = justQuintaNoite;
    }

    public String getJustDomingoManha() {
        return justDomingoManha;
    }

    public void setJustDomingoManha(String justDomingoManha) {
        this.justDomingoManha = justDomingoManha;
    }

    public String getJustDomingoNoite() {
        return justDomingoNoite;
    }

    public void setJustDomingoNoite(String justDomingoNoite) {
        this.justDomingoNoite = justDomingoNoite;
    }

    @Column(name = "just_domingo_manha")
    private String justDomingoManha;

    @Column(name = "just_domingo_noite")
    private String justDomingoNoite;
    private LocalDate semanaInicio;
    private LocalDate semanaFim;

    @ManyToOne
    private Membro membro;

    private Boolean quartaNoite;
    private Boolean quintaNoite;
    private Boolean escolaBiblica;
    private Boolean domingoManha;
    private Boolean domingoNoite;

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

    public boolean isQuartaNoite() {
        return quartaNoite;
    }

    public void setQuartaNoite(boolean quartaNoite) {
        this.quartaNoite = quartaNoite;
    }

    public boolean isQuintaNoite() {
        return quintaNoite;
    }

    public void setQuintaNoite(boolean quintaNoite) {
        this.quintaNoite = quintaNoite;
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
        if (quartaNoite) total++;
        if (quintaNoite) total++;
        if (domingoManha) total++;
        if (domingoNoite) total++;
        this.totalPresencas = total;
    }

    /**
     * Pontuação por presença marcada:
     * quarta = 2 · quinta = 2 · domingo (manhã ou noite) = 4 cada · escola bíblica = 5.
     */
    public int getTotalPontos() {
        int pontos = 0;
        if (Boolean.TRUE.equals(quartaNoite))    pontos += 2;
        if (Boolean.TRUE.equals(quintaNoite))    pontos += 2;
        if (Boolean.TRUE.equals(domingoManha))   pontos += 4;
        if (Boolean.TRUE.equals(domingoNoite))   pontos += 4;
        if (Boolean.TRUE.equals(escolaBiblica))  pontos += 5;
        return pontos;
    }
}
