package com.gestaoigrejaemcelula.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoArrolamento;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "membros", indexes = {
        @Index(name = "idx_membros_celula_id", columnList = "celula_id"),
        @Index(name = "idx_membros_data_nascimento", columnList = "dataNascimento"),
        @Index(name = "idx_membros_status", columnList = "status")
})
public class Membro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------------------------------------
    // DADOS BÁSICOS
    // -------------------------------------------------------

    @Column(nullable = false)
    private String nome;

    private String telefone;
    private String email;

    @Column(length = 14)
    private String cpf;

    @Column(length = 20)
    private String rg;

    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;

    private LocalDate dataNascimento;
    private LocalDate dataConversao;
    private LocalDate dataBatismo;

    @Column(nullable = false, updatable = false)
    private LocalDate dataCadastro;

    @Enumerated(EnumType.STRING)
    private StatusMembro status = StatusMembro.ATIVO;

    @ManyToOne
    @JoinColumn(name = "celula_id")
    @JsonBackReference
    private Celula celula;

    // -------------------------------------------------------
    // FILIAÇÃO E NATURALIDADE
    // -------------------------------------------------------

    private String nomeMae;
    private String nomePai;
    private String nomeConjuge; // ✅ sem acento
    private String naturalidade;

    // -------------------------------------------------------
    // ESCOLARIDADE E PROFISSÃO
    // -------------------------------------------------------

    private String grauEscolaridade;
    private String curso;
    private String profissao;

    // -------------------------------------------------------
    // ENDEREÇO DETALHADO
    // -------------------------------------------------------

    private String endereco;
    private String numero;
    private String bairro;
    private String cidade;

    @Column(length = 9)
    private String cep;

    @Column(length = 2)
    private String uf; // ✅ junto com endereço

    // -------------------------------------------------------
    // DADOS ESPIRITUAIS
    // -------------------------------------------------------

    private Boolean pertenceOutraReligiao;
    private String qualReligiao;

    private Boolean batizadoNasAguas;
    private LocalDate dataBatizadoNasAguas;
    private String igrejaBatizadoNasAguas;

    private Boolean batizadoEspiritoSanto;

    // -------------------------------------------------------
    // ARROLAMENTO
    // -------------------------------------------------------

    @Enumerated(EnumType.STRING)
    private TipoArrolamento tipoArrolamento;

    private String jurisdicaoArrolamento;
    private String arroladoPor;

    // -------------------------------------------------------
    // OUTROS
    // -------------------------------------------------------

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    // -------------------------------------------------------
    // LIFECYCLE
    // -------------------------------------------------------

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDate.now();
    }

    // -------------------------------------------------------
    // GETTERS E SETTERS
    // -------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public EstadoCivil getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(EstadoCivil estadoCivil) { this.estadoCivil = estadoCivil; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public LocalDate getDataConversao() { return dataConversao; }
    public void setDataConversao(LocalDate dataConversao) { this.dataConversao = dataConversao; }

    public LocalDate getDataBatismo() { return dataBatismo; }
    public void setDataBatismo(LocalDate dataBatismo) { this.dataBatismo = dataBatismo; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public StatusMembro getStatus() { return status; }
    public void setStatus(StatusMembro status) { this.status = status; }

    public Celula getCelula() { return celula; }
    public void setCelula(Celula celula) { this.celula = celula; }

    public String getNomeMae() { return nomeMae; }
    public void setNomeMae(String nomeMae) { this.nomeMae = nomeMae; }

    public String getNomePai() { return nomePai; }
    public void setNomePai(String nomePai) { this.nomePai = nomePai; }

    public String getNomeConjuge() { return nomeConjuge; } // ✅ sem acento
    public void setNomeConjuge(String nomeConjuge) { this.nomeConjuge = nomeConjuge; }

    public String getNaturalidade() { return naturalidade; }
    public void setNaturalidade(String naturalidade) { this.naturalidade = naturalidade; }

    public String getGrauEscolaridade() { return grauEscolaridade; }
    public void setGrauEscolaridade(String grauEscolaridade) { this.grauEscolaridade = grauEscolaridade; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public String getProfissao() { return profissao; }
    public void setProfissao(String profissao) { this.profissao = profissao; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public Boolean getPertenceOutraReligiao() { return pertenceOutraReligiao; }
    public void setPertenceOutraReligiao(Boolean pertenceOutraReligiao) { this.pertenceOutraReligiao = pertenceOutraReligiao; }

    public String getQualReligiao() { return qualReligiao; }
    public void setQualReligiao(String qualReligiao) { this.qualReligiao = qualReligiao; }

    public Boolean getBatizadoNasAguas() { return batizadoNasAguas; }
    public void setBatizadoNasAguas(Boolean batizadoNasAguas) { this.batizadoNasAguas = batizadoNasAguas; }

    public LocalDate getDataBatizadoNasAguas() { return dataBatizadoNasAguas; }
    public void setDataBatizadoNasAguas(LocalDate dataBatizadoNasAguas) { this.dataBatizadoNasAguas = dataBatizadoNasAguas; }

    public String getIgrejaBatizadoNasAguas() { return igrejaBatizadoNasAguas; }
    public void setIgrejaBatizadoNasAguas(String igrejaBatizadoNasAguas) { this.igrejaBatizadoNasAguas = igrejaBatizadoNasAguas; }

    public Boolean getBatizadoEspiritoSanto() { return batizadoEspiritoSanto; }
    public void setBatizadoEspiritoSanto(Boolean batizadoEspiritoSanto) { this.batizadoEspiritoSanto = batizadoEspiritoSanto; }

    public TipoArrolamento getTipoArrolamento() { return tipoArrolamento; }
    public void setTipoArrolamento(TipoArrolamento tipoArrolamento) { this.tipoArrolamento = tipoArrolamento; }

    public String getJurisdicaoArrolamento() { return jurisdicaoArrolamento; }
    public void setJurisdicaoArrolamento(String jurisdicaoArrolamento) { this.jurisdicaoArrolamento = jurisdicaoArrolamento; }

    public String getArroladoPor() { return arroladoPor; }
    public void setArroladoPor(String arroladoPor) { this.arroladoPor = arroladoPor; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}