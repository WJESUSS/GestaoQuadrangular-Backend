package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusConvertido;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoArrolamento;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Ficha de Convertido: pessoa que aceitou Jesus mas ainda NÃO é membro.
 * Registrada pela Secretaria com os mesmos dados da ficha de membro.
 * Só se torna MEMBRO quando se batiza na igreja e a Secretaria autoriza
 * a membresia (autorizarMembresia), momento em que um Membro real é criado.
 */
@Entity
@Table(name = "convertidos", indexes = {
        @Index(name = "idx_convertido_status", columnList = "status")
})
public class Convertido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------------------------------------
    // CONTROLE DE FLUXO
    // -------------------------------------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConvertido status = StatusConvertido.AGUARDANDO_BATISMO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    /** Secretário que registrou a ficha */
    @ManyToOne
    @JoinColumn(name = "registrado_por_id")
    private Usuario registradoPor;

    /** Secretário que autorizou a membresia após o batismo */
    @ManyToOne
    @JoinColumn(name = "autorizado_por_id")
    private Usuario autorizadoPor;

    private LocalDateTime dataAutorizacao;

    /** Data do batismo realizado nesta igreja (informada na autorização) */
    private LocalDate dataBatismoIgreja;

    /** ID do membro criado após a autorização */
    private Long membroCriadoId;

    // -------------------------------------------------------
    // DADOS DA FICHA (mesmos campos da ficha de membro)
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

    /** Batismo anterior em OUTRA igreja (não substitui o batismo nesta igreja) */
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
        this.dataRegistro = LocalDateTime.now();
    }

    // -------------------------------------------------------
    // GETTERS E SETTERS
    // -------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatusConvertido getStatus() { return status; }
    public void setStatus(StatusConvertido status) { this.status = status; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }

    public Usuario getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(Usuario registradoPor) { this.registradoPor = registradoPor; }

    public Usuario getAutorizadoPor() { return autorizadoPor; }
    public void setAutorizadoPor(Usuario autorizadoPor) { this.autorizadoPor = autorizadoPor; }

    public LocalDateTime getDataAutorizacao() { return dataAutorizacao; }
    public void setDataAutorizacao(LocalDateTime dataAutorizacao) { this.dataAutorizacao = dataAutorizacao; }

    public LocalDate getDataBatismoIgreja() { return dataBatismoIgreja; }
    public void setDataBatismoIgreja(LocalDate dataBatismoIgreja) { this.dataBatismoIgreja = dataBatismoIgreja; }

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
