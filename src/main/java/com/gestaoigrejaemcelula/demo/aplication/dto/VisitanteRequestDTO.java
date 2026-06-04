package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.enums.OrigemVisitante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class VisitanteRequestDTO {
    @NotNull private Long celulaId;
    @NotBlank private String nome;
    private String telefone;
    private String email;
    private LocalDate dataPrimeiraVisita;
    private OrigemVisitante origem;
    private String responsavelAcompanhamento;
    private DecisaoEspiritual decisaoEspiritual;
    private boolean ativo;

    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getDataPrimeiraVisita() { return dataPrimeiraVisita; }
    public void setDataPrimeiraVisita(LocalDate dataPrimeiraVisita) { this.dataPrimeiraVisita = dataPrimeiraVisita; }
    public OrigemVisitante getOrigem() { return origem; }
    public void setOrigem(OrigemVisitante origem) { this.origem = origem; }
    public String getResponsavelAcompanhamento() { return responsavelAcompanhamento; }
    public void setResponsavelAcompanhamento(String responsavelAcompanhamento) { this.responsavelAcompanhamento = responsavelAcompanhamento; }
    public DecisaoEspiritual getDecisaoEspiritual() { return decisaoEspiritual; }
    public void setDecisaoEspiritual(DecisaoEspiritual decisaoEspiritual) { this.decisaoEspiritual = decisaoEspiritual; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
