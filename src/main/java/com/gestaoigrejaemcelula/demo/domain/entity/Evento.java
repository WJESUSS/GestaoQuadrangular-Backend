package com.gestaoigrejaemcelula.demo.domain.entity;



import com.gestaoigrejaemcelula.demo.domain.enums.TipoEvento;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "eventos")

public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipo;

    private LocalDate data;
    private String local;
    private String anfitriao;

    // membros participantes
    @ManyToMany
    @JoinTable(
            name = "evento_membros",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private List<Membro> membros;

    // visitantes participantes
    @ManyToMany
    @JoinTable(
            name = "evento_visitantes",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "visitante_id")
    )
    private List<Visitante> visitantes;

    private Integer totalPresentes;
    private Integer aceitaramJesus;
    private Integer batizados;

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

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getAnfitriao() {
        return anfitriao;
    }

    public void setAnfitriao(String anfitriao) {
        this.anfitriao = anfitriao;
    }

    public List<Membro> getMembros() {
        return membros;
    }

    public void setMembros(List<Membro> membros) {
        this.membros = membros;
    }

    public List<Visitante> getVisitantes() {
        return visitantes;
    }

    public void setVisitantes(List<Visitante> visitantes) {
        this.visitantes = visitantes;
    }

    public Integer getTotalPresentes() {
        return totalPresentes;
    }

    public void setTotalPresentes(Integer totalPresentes) {
        this.totalPresentes = totalPresentes;
    }

    public Integer getAceitaramJesus() {
        return aceitaramJesus;
    }

    public void setAceitaramJesus(Integer aceitaramJesus) {
        this.aceitaramJesus = aceitaramJesus;
    }

    public Integer getBatizados() {
        return batizados;
    }

    public void setBatizados(Integer batizados) {
        this.batizados = batizados;
    }
}
