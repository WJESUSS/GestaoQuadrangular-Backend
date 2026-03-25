package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.TipoEncontro;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FichaEncontroResponseDTO {

    private Long id;

    // Dados pessoais
    private String nome;
    private LocalDate dataNascimento;
    private String endereco;
    private String bairro;
    private String cidade;
    private String telefone;
    private String sexo;
    private String estadoCivil;
    private String rg;
    private String estado;
    private Double peso;
    private Double altura;

    // Saúde
    private boolean tomaMedicamento;
    private String qualMedicamento;
    private boolean temProblemasSaude;
    private String qualProblemaSaude;
    private boolean temApneia;

    // Contatos e líderes
    private String nomeConvidador;
    private String celulaConvidador;
    private String nomeLiderCelula;
    private String nomeFamiliarContato;
    private String telefoneFamiliarContato;

    // Participação e célula
    private boolean frequentaCelula;
    private String nomeCelula;
    private List<String> outrosParticipantes;

    // Decisões espirituais
    private boolean aceitouJesus;
    private boolean jaEraCristao;

    // Dados do encontro
    private String nomeEncontro;
    private String localEncontro;
    private TipoEncontro tipoEncontro;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    // Campos de auditoria / status (comuns em ResponseDTO)
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String status;               // Ex: "PENDENTE", "APROVADA", "REJEITADA"
    private String criadoPor;            // Username ou nome do usuário que criou
    private Long usuarioId;              // ID do usuário logado que enviou (opcional)

    // Construtor vazio
    public FichaEncontroResponseDTO() {}

    // Getters e Setters (gerados automaticamente com Lombok ou manualmente)

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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
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

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public boolean isTomaMedicamento() {
        return tomaMedicamento;
    }

    public void setTomaMedicamento(boolean tomaMedicamento) {
        this.tomaMedicamento = tomaMedicamento;
    }

    public String getQualMedicamento() {
        return qualMedicamento;
    }

    public void setQualMedicamento(String qualMedicamento) {
        this.qualMedicamento = qualMedicamento;
    }

    public boolean isTemProblemasSaude() {
        return temProblemasSaude;
    }

    public void setTemProblemasSaude(boolean temProblemasSaude) {
        this.temProblemasSaude = temProblemasSaude;
    }

    public String getQualProblemaSaude() {
        return qualProblemaSaude;
    }

    public void setQualProblemaSaude(String qualProblemaSaude) {
        this.qualProblemaSaude = qualProblemaSaude;
    }

    public boolean isTemApneia() {
        return temApneia;
    }

    public void setTemApneia(boolean temApneia) {
        this.temApneia = temApneia;
    }

    public String getNomeConvidador() {
        return nomeConvidador;
    }

    public void setNomeConvidador(String nomeConvidador) {
        this.nomeConvidador = nomeConvidador;
    }

    public String getCelulaConvidador() {
        return celulaConvidador;
    }

    public void setCelulaConvidador(String celulaConvidador) {
        this.celulaConvidador = celulaConvidador;
    }

    public String getNomeLiderCelula() {
        return nomeLiderCelula;
    }

    public void setNomeLiderCelula(String nomeLiderCelula) {
        this.nomeLiderCelula = nomeLiderCelula;
    }

    public String getNomeFamiliarContato() {
        return nomeFamiliarContato;
    }

    public void setNomeFamiliarContato(String nomeFamiliarContato) {
        this.nomeFamiliarContato = nomeFamiliarContato;
    }

    public String getTelefoneFamiliarContato() {
        return telefoneFamiliarContato;
    }

    public void setTelefoneFamiliarContato(String telefoneFamiliarContato) {
        this.telefoneFamiliarContato = telefoneFamiliarContato;
    }

    public boolean isFrequentaCelula() {
        return frequentaCelula;
    }

    public void setFrequentaCelula(boolean frequentaCelula) {
        this.frequentaCelula = frequentaCelula;
    }

    public String getNomeCelula() {
        return nomeCelula;
    }

    public void setNomeCelula(String nomeCelula) {
        this.nomeCelula = nomeCelula;
    }

    public List<String> getOutrosParticipantes() {
        return outrosParticipantes;
    }

    public void setOutrosParticipantes(List<String> outrosParticipantes) {
        this.outrosParticipantes = outrosParticipantes;
    }

    public boolean isAceitouJesus() {
        return aceitouJesus;
    }

    public void setAceitouJesus(boolean aceitouJesus) {
        this.aceitouJesus = aceitouJesus;
    }

    public boolean isJaEraCristao() {
        return jaEraCristao;
    }

    public void setJaEraCristao(boolean jaEraCristao) {
        this.jaEraCristao = jaEraCristao;
    }

    public String getNomeEncontro() {
        return nomeEncontro;
    }

    public void setNomeEncontro(String nomeEncontro) {
        this.nomeEncontro = nomeEncontro;
    }

    public String getLocalEncontro() {
        return localEncontro;
    }

    public void setLocalEncontro(String localEncontro) {
        this.localEncontro = localEncontro;
    }

    public TipoEncontro getTipoEncontro() {
        return tipoEncontro;
    }

    public void setTipoEncontro(TipoEncontro tipoEncontro) {
        this.tipoEncontro = tipoEncontro;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}