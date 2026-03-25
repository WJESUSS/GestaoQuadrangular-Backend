package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.Sexo;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEncontro;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fichas_encontro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FichaEncontro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1️⃣ DADOS PESSOAIS
    @Column(nullable = false)
    private String nomeConvidado;

    private LocalDate dataNascimento;
    private String endereco;
    private String bairro;
    private String cidade;

    @Column(nullable = false)
    private String telefone;

    private String rg;
    private String estado;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;

    private Double peso;
    private Double altura;

    // 2️⃣ SAÚDE
    @Column(name = "tem_apneia", nullable = false, columnDefinition = "boolean default false")
    private boolean temApneia = false;

    @Column(name = "toma_medicamento", nullable = false, columnDefinition = "boolean default false")
    private boolean tomaMedicamento = false;

    private String qualMedicamento;

    @Column(name = "tem_problemas_saude", nullable = false, columnDefinition = "boolean default false")
    private boolean temProblemasSaude = false;

    private String qualProblemaSaude;

    // 3️⃣ FREQUÊNCIA CÉLULA
    private boolean frequentaCelula;
    private String nomeCelula;

    // 4️⃣ QUEM CONVIDOU
    @Column(nullable = false)
    private String nomeConvidador;
    private String celulaConvidador;
    private String funcaoConvidador;

    // 5️⃣ DADOS DO ENCONTRO
    @Column(nullable = false)
    private String nomeEncontro;

    @Column(nullable = false)
    private LocalDate dataInicio;

    private LocalDate dataFim;

    @Column(nullable = false)
    private String localEncontro;

    // 6️⃣ ACOMPANHAMENTO
    private String liderResponsavel;
    private String discipulador;

    @ElementCollection
    @CollectionTable(name = "ficha_outros_participantes", joinColumns = @JoinColumn(name = "ficha_id"))
    @Column(name = "nome_participante")
    private List<String> outrosParticipantes;

    // 7️⃣ DECISÕES ESPIRITUAIS
    private boolean aceitouJesus;
    private boolean jaEraCristao;

    // 8️⃣ VÍNCULOS
    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @Enumerated(EnumType.STRING)
    private TipoEncontro tipoEncontro;

    // 9️⃣ CONTATOS DE EMERGÊNCIA
    private String nomeFamiliarContato;
    private String telefoneFamiliarContato;

    // 10️⃣ AUDITORIA (essencial para findByUsuarioLogado)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;  // ← Campo que associa ao usuário que criou a ficha

    private String criadoPor;  // username ou nome do criador

    @Enumerated(EnumType.STRING)
    private StatusFicha status = StatusFicha.PENDENTE;

    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataAtualizacao;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // ... outros campos e anotações ...

    private String nomeLiderCelula;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeConvidado() {
        return nomeConvidado;
    }

    public void setNomeConvidado(String nomeConvidado) {
        this.nomeConvidado = nomeConvidado;
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

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
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

    public boolean isTemApneia() {
        return temApneia;
    }

    public void setTemApneia(boolean temApneia) {
        this.temApneia = temApneia;
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

    public String getFuncaoConvidador() {
        return funcaoConvidador;
    }

    public void setFuncaoConvidador(String funcaoConvidador) {
        this.funcaoConvidador = funcaoConvidador;
    }

    public String getNomeEncontro() {
        return nomeEncontro;
    }

    public void setNomeEncontro(String nomeEncontro) {
        this.nomeEncontro = nomeEncontro;
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

    public String getLocalEncontro() {
        return localEncontro;
    }

    public void setLocalEncontro(String localEncontro) {
        this.localEncontro = localEncontro;
    }

    public String getLiderResponsavel() {
        return liderResponsavel;
    }

    public void setLiderResponsavel(String liderResponsavel) {
        this.liderResponsavel = liderResponsavel;
    }

    public String getDiscipulador() {
        return discipulador;
    }

    public void setDiscipulador(String discipulador) {
        this.discipulador = discipulador;
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

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public TipoEncontro getTipoEncontro() {
        return tipoEncontro;
    }

    public void setTipoEncontro(TipoEncontro tipoEncontro) {
        this.tipoEncontro = tipoEncontro;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public StatusFicha getStatus() {
        return status;
    }

    public void setStatus(StatusFicha status) {
        this.status = status;
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

    // Getter
    public String getNomeLiderCelula() {
        return nomeLiderCelula;
    }

    // Setter (se não estiver usando Lombok @Setter)
    public void setNomeLiderCelula(String nomeLiderCelula) {
        this.nomeLiderCelula = nomeLiderCelula;
    }

    public enum StatusFicha {
        PENDENTE, APROVADA, REJEITADA, CANCELADA
    }
}