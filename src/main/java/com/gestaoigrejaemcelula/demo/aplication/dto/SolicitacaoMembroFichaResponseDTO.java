package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.SolicitacaoMembroFicha;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusSolicitacaoMembro;

import java.time.LocalDateTime;

public class SolicitacaoMembroFichaResponseDTO {

    private Long id;
    private StatusSolicitacaoMembro status;
    private String statusDescricao;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataDecisao;

    // Quem enviou
    private Long liderId;
    private String liderNome;

    // Célula do líder
    private Long celulaId;
    private String celulaDescricao;

    // Quem avaliou
    private Long secretarioId;
    private String secretarioNome;

    // Resultado da decisão
    private String motivoRejeicao;
    private Long membroCriadoId;

    // Dados da ficha
    private String nome;
    private String telefone;
    private String email;
    private String cpf;
    private String rg;

    public SolicitacaoMembroFichaResponseDTO() {}

    public SolicitacaoMembroFichaResponseDTO(SolicitacaoMembroFicha s) {
        this.id = s.getId();
        this.status = s.getStatus();
        this.statusDescricao = s.getStatus().getDescricao();
        this.dataSolicitacao = s.getDataSolicitacao();
        this.dataDecisao = s.getDataDecisao();

        if (s.getLider() != null) {
            this.liderId = s.getLider().getId();
            this.liderNome = s.getLider().getNome();
        }

        if (s.getCelula() != null) {
            this.celulaId = s.getCelula().getId();
            this.celulaDescricao = s.getCelula().getNome();
        }

        if (s.getSecretario() != null) {
            this.secretarioId = s.getSecretario().getId();
            this.secretarioNome = s.getSecretario().getNome();
        }

        this.motivoRejeicao = s.getMotivoRejeicao();
        this.membroCriadoId = s.getMembroCriadoId();

        this.nome = s.getNome();
        this.telefone = s.getTelefone();
        this.email = s.getEmail();
        this.cpf = s.getCpf();
        this.rg = s.getRg();
    }

    // ── Getters / Setters ──────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatusSolicitacaoMembro getStatus() { return status; }
    public void setStatus(StatusSolicitacaoMembro status) { this.status = status; }

    public String getStatusDescricao() { return statusDescricao; }
    public void setStatusDescricao(String statusDescricao) { this.statusDescricao = statusDescricao; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public LocalDateTime getDataDecisao() { return dataDecisao; }
    public void setDataDecisao(LocalDateTime dataDecisao) { this.dataDecisao = dataDecisao; }

    public Long getLiderId() { return liderId; }
    public void setLiderId(Long liderId) { this.liderId = liderId; }

    public String getLiderNome() { return liderNome; }
    public void setLiderNome(String liderNome) { this.liderNome = liderNome; }

    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }

    public String getCelulaDescricao() { return celulaDescricao; }
    public void setCelulaDescricao(String celulaDescricao) { this.celulaDescricao = celulaDescricao; }

    public Long getSecretarioId() { return secretarioId; }
    public void setSecretarioId(Long secretarioId) { this.secretarioId = secretarioId; }

    public String getSecretarioNome() { return secretarioNome; }
    public void setSecretarioNome(String secretarioNome) { this.secretarioNome = secretarioNome; }

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
}