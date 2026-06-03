package com.gestaoigrejaemcelula.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "missao70")
public class Missao70 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String nomeAnfitriao;

    @Column(nullable = false)
    private String endereco;

    private String telefoneContato;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private int encontrosRestantes = 4;

    @Column(nullable = false)
    private int proximaSemana = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMissao70 status = StatusMissao70.EM_ANDAMENTO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celula_id", nullable = true)
    @JsonIgnoreProperties({"membros", "lider", "pastor", "secretario", "hibernateLazyInitializer"})
    private Celula celula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lider_id", nullable = true)
    @JsonIgnoreProperties({"celula", "dataCadastro", "hibernateLazyInitializer"})
    private Membro lider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auxiliar_id", nullable = true)
    @JsonIgnoreProperties({"celula", "dataCadastro", "hibernateLazyInitializer"})
    private Membro auxiliar;

    @ManyToMany
    @JoinTable(
            name = "missao70_visitantes",
            joinColumns = @JoinColumn(name = "missao70_id"),
            inverseJoinColumns = @JoinColumn(name = "visitante_id")
    )
    @JsonIgnoreProperties({"celula", "hibernateLazyInitializer"})
    private List<Visitante> visitantes = new ArrayList<>();

    @OneToMany(mappedBy = "missao70", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EncontroMissao70> encontros = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getNomeAnfitriao() { return nomeAnfitriao; }
    public void setNomeAnfitriao(String nomeAnfitriao) { this.nomeAnfitriao = nomeAnfitriao; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public int getEncontrosRestantes() { return encontrosRestantes; }
    public void setEncontrosRestantes(int encontrosRestantes) { this.encontrosRestantes = encontrosRestantes; }
    public int getProximaSemana() { return proximaSemana; }
    public void setProximaSemana(int proximaSemana) { this.proximaSemana = proximaSemana; }
    public StatusMissao70 getStatus() { return status; }
    public void setStatus(StatusMissao70 status) { this.status = status; }
    public Celula getCelula() { return celula; }
    public void setCelula(Celula celula) { this.celula = celula; }
    public Membro getLider() { return lider; }
    public void setLider(Membro lider) { this.lider = lider; }
    public Membro getAuxiliar() { return auxiliar; }
    public void setAuxiliar(Membro auxiliar) { this.auxiliar = auxiliar; }
    public List<Visitante> getVisitantes() { return visitantes; }
    public void setVisitantes(List<Visitante> visitantes) { this.visitantes = visitantes; }
    public List<EncontroMissao70> getEncontros() { return encontros; }
    public void setEncontros(List<EncontroMissao70> encontros) { this.encontros = encontros; }
}