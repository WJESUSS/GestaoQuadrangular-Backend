package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.MotivoNaoRealizacaoCelula;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_relatorio_celula_id", columnList = "celula_id"),
        @Index(name = "idx_relatorio_data_reuniao", columnList = "dataReuniao")
})
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Celula celula;

    private LocalDate dataReuniao;

    @Column(columnDefinition = "TEXT")
    private String estudo;

    private Integer quantidadeVisitantes = 0;

    @Column(nullable = true)
    private Boolean realizada = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MotivoNaoRealizacaoCelula motivoNaoRealizacao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "relatorio_membros_presenca",
            joinColumns = @JoinColumn(name = "relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private List<Membro> presentes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "relatorio_visitantes_presenca",
            joinColumns = @JoinColumn(name = "relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "visitante_id")
    )
    private Set<Visitante> visitantesPresentes = new HashSet<>();

    private LocalDateTime dataCadastro = LocalDateTime.now();

    // Apenas métodos de negócio — getters/setters gerados pelo Lombok
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