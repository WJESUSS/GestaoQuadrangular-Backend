package com.gestaoigrejaemcelula.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "celulas")
public class Celula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Protege contra o loop no Lider
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lider_id", nullable = false)
    @JsonIgnoreProperties({"celula", "senha", "authorities", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"})
    private Usuario lider;

    // Protege contra o loop no Pastor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pastor_id")
    @JsonIgnoreProperties({"celula", "senha", "authorities", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"})
    private Usuario pastor;

    // Protege contra o loop no Secretário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secretario_id")
    @JsonIgnoreProperties({"celula", "senha", "authorities", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"})
    private Usuario secretario;

    private String anfitriao;
    private String endereco;
    private String bairro;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_multiplicacao", nullable = false, columnDefinition = "varchar(255) default 'NORMAL'")
    private StatusMultiplicacao statusMultiplicacao = StatusMultiplicacao.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "dia_semana")
    private DayOfWeek diaSemana;

    @Column(nullable = false)
    private LocalTime horario;

    @Column(nullable = false, name = "ativo")
    private boolean ativa = true;

    private LocalDateTime dataSolicitacaoMultiplicacao;
    private String motivoSolicitacao;

    // Protege a lista de membros para não voltarem para a célula recursivamente
    @OneToMany(mappedBy = "celula", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Membro> membros = new ArrayList<>();

    public enum StatusMultiplicacao {
        NORMAL, SOLICITADO, EM_ANALISE, APROVADO, REJEITADO
    }

    // --- MÉTODOS AUXILIARES ---
    public int getQuantidadeMembrosAtivos() {
        if (membros == null) return 0;
        return (int) membros.stream()
                .filter(m -> m.getStatus() == StatusMembro.ATIVO)
                .count();
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Usuario getLider() {
        return lider;
    }

    public void setLider(Usuario lider) {
        this.lider = lider;
    }

    public Usuario getPastor() {
        return pastor;
    }

    public void setPastor(Usuario pastor) {
        this.pastor = pastor;
    }

    public Usuario getSecretario() {
        return secretario;
    }

    public void setSecretario(Usuario secretario) {
        this.secretario = secretario;
    }

    public String getAnfitriao() {
        return anfitriao;
    }

    public void setAnfitriao(String anfitriao) {
        this.anfitriao = anfitriao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public List<Membro> getMembros() {
        return membros;
    }

    public void setMembros(List<Membro> membros) {
        this.membros = membros;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public StatusMultiplicacao getStatusMultiplicacao() {
        return statusMultiplicacao;
    }

    public void setStatusMultiplicacao(StatusMultiplicacao statusMultiplicacao) {
        this.statusMultiplicacao = statusMultiplicacao;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getDataSolicitacaoMultiplicacao() {
        return dataSolicitacaoMultiplicacao;
    }

    public void setDataSolicitacaoMultiplicacao(LocalDateTime dataSolicitacaoMultiplicacao) {
        this.dataSolicitacaoMultiplicacao = dataSolicitacaoMultiplicacao;
    }

    public String getMotivoSolicitacao() {
        return motivoSolicitacao;
    }

    public void setMotivoSolicitacao(String motivoSolicitacao) {
        this.motivoSolicitacao = motivoSolicitacao;
    }

}