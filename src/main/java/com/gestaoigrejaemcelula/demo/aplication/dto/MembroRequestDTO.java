package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoArrolamento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class MembroRequestDTO {

    // -------------------------------------------------------
    // DADOS BÁSICOS
    // -------------------------------------------------------

    @NotBlank
    private String nome;

    @NotBlank
    private String telefone;

    @Email
    private String email;

    private String cpf;
    private String rg;

    private EstadoCivil estadoCivil;
    private StatusMembro status;

    private LocalDate dataNascimento;
    private LocalDate dataConversao;
    private LocalDate dataBatismo;

    // -------------------------------------------------------
    // FILIAÇÃO E NATURALIDADE
    // -------------------------------------------------------

    private String nomeMae;
    private String nomePai;
    private String nomeConjuge;
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
    private String cep;
    private String uf;

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

    private TipoArrolamento tipoArrolamento;
    private String jurisdicaoArrolamento;
    private String arroladoPor;

    // -------------------------------------------------------
    // OUTROS
    // -------------------------------------------------------

    private String observacoes;

    // -------------------------------------------------------
    // GETTERS E SETTERS
    // -------------------------------------------------------

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

    public StatusMembro getStatus() { return status; }
    public void setStatus(StatusMembro status) { this.status = status; }

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