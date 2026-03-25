package com.gestaoigrejaemcelula.demo.domain.entity;


import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.enums.OrigemVisitante;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "visitantes")

public class Visitante {
    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Column(nullable = false)
    private boolean ativo = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "decisao_espiritual")
    private DecisaoEspiritual decisaoEspiritual;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private Boolean convertido = false;

    private String telefone;
    private String email;

    private LocalDate dataPrimeiraVisita;

    @Enumerated(EnumType.STRING)
    private OrigemVisitante origem;

    private String responsavelAcompanhamento;




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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataPrimeiraVisita() {
        return dataPrimeiraVisita;
    }

    public void setDataPrimeiraVisita(LocalDate dataPrimeiraVisita) {
        this.dataPrimeiraVisita = dataPrimeiraVisita;
    }

    public OrigemVisitante getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemVisitante origem) {
        this.origem = origem;
    }

    public String getResponsavelAcompanhamento() {
        return responsavelAcompanhamento;
    }

    public void setResponsavelAcompanhamento(String responsavelAcompanhamento) {
        this.responsavelAcompanhamento = responsavelAcompanhamento;
    }

    public DecisaoEspiritual getDecisaoEspiritual() {
        return decisaoEspiritual;
    }

    public void setDecisaoEspiritual(DecisaoEspiritual decisaoEspiritual) {
        this.decisaoEspiritual = decisaoEspiritual;
    }

    public Boolean getConvertido() {
        return convertido;
    }

    public void setConvertido(Boolean convertido) {
        this.convertido = convertido;
    }

    @ManyToOne // Indica que muitos visitantes pertencem a uma célula
    @JoinColumn(name = "celula_id") // Nome da coluna no banco de dados
    private Celula celula;

    public void setCelula(Celula celula) {
        this.celula = celula;
    }

    public Celula getCelula() {
        return celula;
    }


}
