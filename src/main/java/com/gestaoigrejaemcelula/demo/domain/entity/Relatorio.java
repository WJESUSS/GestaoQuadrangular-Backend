package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Celula celula;

    private LocalDate dataReuniao;

    @Column(columnDefinition = "TEXT")
    private String estudo;

    private Integer quantidadeVisitantes = 0;

    @ManyToMany
    @JoinTable(
            name = "relatorio_membros_presenca",
            joinColumns = @JoinColumn(name = "relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private List<Membro> presentes = new java.util.ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "relatorio_visitantes_presenca",
            joinColumns = @JoinColumn(name = "relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "visitante_id")
    )
    private List<Visitante> visitantesPresentes = new java.util.ArrayList<>();

    private LocalDateTime dataCadastro = LocalDateTime.now();

    public int getQuantidadeMembros() {
        return presentes.size();
    }

    public int getQuantidadeVisitantesCadastrados() {
        return visitantesPresentes.size();
    }

    public int getTotalVisitantes() {
        return getQuantidadeVisitantesCadastrados() + quantidadeVisitantes;
    }

    public int getTotalPresentes() {
        return getQuantidadeMembros() + getTotalVisitantes();
    }
}
