package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusSolicitacaoMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoArrolamento;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Ficha de solicitação de cadastro de novo membro enviada pelo Líder.
 * Fica PENDENTE até a Secretaria aprovar ou rejeitar.
 * Ao ser APROVADA, um Membro real é criado automaticamente.
 */
@Entity
@Table(name = "solicitacoes_membro", indexes = {
        @Index(name = "idx_solicitacao_status", columnList = "status"),
        @Index(name = "idx_solicitacao_celula", columnList = "celula_id")
})
public class SolicitacaoMembroFicha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------------------------------------
    // CONTROLE DE FLUXO
    // -------------------------------------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacaoMembro status = StatusSolicitacaoMembro.PENDENTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    private LocalDateTime dataDecisao;

    /** Quem enviou (líder) */
    @ManyToOne
    @JoinColumn(name = "lider_id")
    private Usuario lider;

    /** Célula do líder que enviou */
    @ManyToOne
    @JoinColumn(name = "celula_id")
    private Celula celula;

    /** Secretário que avaliou */
    @ManyToOne
    @JoinColumn(name = "secretario_id")
    private Usuario secretario;

    /** Motivo de rejeição (preenchido ao rejeitar) */
    @Column(columnDefinition = "TEXT")
    private String motivoRejeicao;

    /** ID do membro criado após aprovação */
    private Long membroCriadoId;

    // -------------------------------------------------------
    // DADOS DA FICHA (espelho de MembroRequestDTO)
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

    private String nomeMae;
    private String nomePai;
    private String nomeConjuge;
    private String naturalidade;

    private String grauEscolaridade;
    private String curso;
    private String profissao;

    private String endereco;
    private String numero;
    private String bairro;
    private String cidade;

    @Column(length = 9)
    private String cep;

    @Column(length = 2)
    private String uf;

    private Boolean pertenceOutraReligiao;
    private String qualReligiao;

    private Boolean batizadoNasAguas;
    private LocalDate dataBatizadoNasAguas;
    private String igrejaBatizadoNasAguas;

    private Boolean batizadoEspiritoSanto;

    @Enumerated(EnumType.STRING)
    private TipoArrolamento tipoArrolamento;

    private String jurisdicaoArrolamento;
    private String arroladoPor;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    // -------------------------------------------------------
    // LIFECYCLE
    // -------------------------------------------------------

    @PrePersist
    public void prePersist() {
        this.dataSolicitacao = LocalDateTime.now();
    }

    // -------------------------------------------------------
    // GETTERS E SETTERS
    // -------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatusSolicitacaoMembro getStatus() { return status; }
    public void setStatus(StatusSolicitacaoMembro status) { this.status = status; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public LocalDateTime getDataDecisao() { return dataDecisao; }
    public void setDataDecisao(LocalDateTime dataDecisao) { this.dataDecisao = dataDecisao; }

    public Usuario getLider() { return lider; }
    public void setLider(Usuario lider) { this.lider = lider; }

    public Celula getCelula() { return celula; }
    public void setCelula(Celula celula) { this.celula = celula; }

    public Usuario getSecretario() { return secretario; }
    public void setSecretario(Usuario secretario) { this.secretario = secretario; }

    public String getMotivoRejeicao() { return motivoRejeicao; }
    public void setMotivoRejeicao(String motivoRejeicao) { this.motivoRejeicao = motivoRejeicao; }

    public Long getMembroCriadoId() { return membroCriadoId; }
    public void setMembroCriadoId(Long membroCriadoId) { this.membroCriadoId = membroCriadoId; }

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

    public String getNomeMae() { return nomeMae; }
    public void setNomeMae(String nomeMae) { this.nomeMae = nomeMae; }

    public String getNomePai() { return nomePai; }
    public void setNomePai(String nomePai) { this.nomePai = nomePai; }

    public String getNomeConjuge() { return nomeConjuge; }
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