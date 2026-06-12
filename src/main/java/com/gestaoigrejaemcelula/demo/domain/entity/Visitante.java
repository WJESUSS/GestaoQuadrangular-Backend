package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.enums.OrigemVisitante;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "visitantes", indexes = {
        @Index(name = "idx_visitantes_celula_id", columnList = "celula_id"),
        @Index(name = "idx_visitantes_decisao_ativo", columnList = "celula_id, decisao_espiritual, ativo")
})
public class Visitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String telefone;
    private String email;
    private LocalDate dataPrimeiraVisita;

    @Enumerated(EnumType.STRING)
    private OrigemVisitante origem;

    private String responsavelAcompanhamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "decisao_espiritual")
    private DecisaoEspiritual decisaoEspiritual;

    @Column(nullable = false)
    private Boolean convertido = false;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean arquivado = false;

    private LocalDate dataArquivamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celula_id")
    private Celula celula;
}