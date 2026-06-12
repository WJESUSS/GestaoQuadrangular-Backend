package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.enums.OrigemVisitante;

import java.time.LocalDate;

public class VisitanteResponseDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private LocalDate dataPrimeiraVisita;
    private OrigemVisitante origem;
    private String responsavelAcompanhamento;
    private String decisaoEspiritual;
    private boolean ativo;
    private boolean arquivado;
    private LocalDate dataArquivamento;
    private String motivoArquivamento;
    private String celula; // ← nome da célula para o frontend

    public VisitanteResponseDTO() {}

    public VisitanteResponseDTO(Visitante visitante) {
        this.id                        = visitante.getId();
        this.nome                      = visitante.getNome();
        this.telefone                  = visitante.getTelefone();
        this.email                     = visitante.getEmail();
        this.dataPrimeiraVisita        = visitante.getDataPrimeiraVisita();
        this.origem                    = visitante.getOrigem();
        this.responsavelAcompanhamento = visitante.getResponsavelAcompanhamento();
        this.decisaoEspiritual         = visitante.getDecisaoEspiritual() != null
                ? visitante.getDecisaoEspiritual().toString() : "NENHUMA";
        this.ativo                     = visitante.isAtivo();
        this.arquivado                 = visitante.isArquivado();
        this.dataArquivamento          = visitante.getDataArquivamento();
        // Pega o nome da célula se o visitante estiver vinculado
        this.celula                    = visitante.getCelula() != null
                ? visitante.getCelula().getNome() : null;
    }

    // ── getters / setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDataPrimeiraVisita() { return dataPrimeiraVisita; }
    public void setDataPrimeiraVisita(LocalDate d) { this.dataPrimeiraVisita = d; }

    public OrigemVisitante getOrigem() { return origem; }
    public void setOrigem(OrigemVisitante origem) { this.origem = origem; }

    public String getResponsavelAcompanhamento() { return responsavelAcompanhamento; }
    public void setResponsavelAcompanhamento(String r) { this.responsavelAcompanhamento = r; }

    public String getDecisaoEspiritual() { return decisaoEspiritual; }
    public void setDecisaoEspiritual(String d) { this.decisaoEspiritual = d; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public boolean isArquivado() { return arquivado; }
    public void setArquivado(boolean arquivado) { this.arquivado = arquivado; }

    public LocalDate getDataArquivamento() { return dataArquivamento; }
    public void setDataArquivamento(LocalDate d) { this.dataArquivamento = d; }

    public String getMotivoArquivamento() { return motivoArquivamento; }
    public void setMotivoArquivamento(String m) { this.motivoArquivamento = m; }

    public String getCelula() { return celula; }
    public void setCelula(String celula) { this.celula = celula; }
}