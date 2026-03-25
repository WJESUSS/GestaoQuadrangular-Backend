package com.gestaoigrejaemcelula.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "membros")

public class Membro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)

    private String nome;
    private String endereco;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    @Column(length = 14)
    private String cpf;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;


    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }


    @Column(nullable = false, updatable = false)
    private LocalDate dataCadastro;

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDate.now();
    }



    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Enumerated(EnumType.STRING)
    private StatusMembro status = StatusMembro.ATIVO;

    @ManyToOne
    @JoinColumn(name = "celula_id")
    @JsonBackReference
    private Celula celula; // pode ser null

    public Long getId() {
        return id;
    }

    private LocalDate dataConversao;
    private LocalDate dataBatismo;


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

    public StatusMembro getStatus() {
        return status;
    }

    public void setStatus(StatusMembro status) {
        this.status = status;
    }

    public Celula getCelula() {
        return celula;
    }

    public void setCelula(Celula celula) {
        this.celula = celula;
    }

    public LocalDate getDataConversao() {
        return dataConversao;
    }

    public void setDataConversao(LocalDate dataConversao) {
        this.dataConversao = dataConversao;
    }

    public LocalDate getDataBatismo() {
        return dataBatismo;
    }

    public void setDataBatismo(LocalDate dataBatismo) {
        this.dataBatismo = dataBatismo;
    }

// getters e setters
}

