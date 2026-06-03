package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.enums.OrigemVisitante;
// Se você usar um Enum para DecisaoEspiritual, importe-o aqui. Exemplo:
// import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;

import java.time.LocalDate;

public class VisitanteResponseDTO {

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private LocalDate dataPrimeiraVisita;
    private OrigemVisitante origem;
    private String responsavelAcompanhamento;
    // Adicionado aqui (Ajuste o tipo para o seu Enum se não for String)
    private String decisaoEspiritual;
    private boolean ativo;

    public VisitanteResponseDTO() {}

    public VisitanteResponseDTO(Visitante visitante) {
        this.id                        = visitante.getId();
        this.nome                      = visitante.getNome();
        this.telefone                  = visitante.getTelefone();
        this.email                     = visitante.getEmail();
        this.dataPrimeiraVisita        = visitante.getDataPrimeiraVisita();
        this.origem                    = visitante.getOrigem();
        this.responsavelAcompanhamento = visitante.getResponsavelAcompanhamento();
        // Mapeia o campo da entidade para o DTO (Trate para String se na Entidade for Enum)
        this.decisaoEspiritual         = visitante.getDecisaoEspiritual() != null ? visitante.getDecisaoEspiritual().toString() : "NENHUMA";
        this.ativo                     = visitante.isAtivo();
    }

    // ── getters / setters ───────────────────────────────────────

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
    public void setResponsavelAcompanhamento(String r) { this.responsavelAcompanhamento = r; }

    // Getter e Setter adicionados
    public String getDecisaoEspiritual() { return decisaoEspiritual; }
    public void setDecisaoEspiritual(String decisaoEspiritual) { this.decisaoEspiritual = decisaoEspiritual; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}