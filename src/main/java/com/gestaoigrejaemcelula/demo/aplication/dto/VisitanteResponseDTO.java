package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.OrigemVisitante;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import java.time.LocalDate;

public class VisitanteResponseDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private LocalDate dataPrimeiraVisita;
    private OrigemVisitante origem;
    private String responsavelAcompanhamento;
    private boolean ativo; // Mantemos apenas se ele está ativo na lista de visitas

    public VisitanteResponseDTO() {}

    // Construtor de conversão atualizado (sem o campo convertido)
    public VisitanteResponseDTO(Visitante visitante) {
        this.id = visitante.getId();
        this.nome = visitante.getNome();
        this.telefone = visitante.getTelefone();
        this.email = visitante.getEmail();
        this.dataPrimeiraVisita = visitante.getDataPrimeiraVisita();
        this.origem = visitante.getOrigem();
        this.responsavelAcompanhamento = visitante.getResponsavelAcompanhamento();
        this.ativo = visitante.isAtivo();
    }

    // GETTERS E SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}