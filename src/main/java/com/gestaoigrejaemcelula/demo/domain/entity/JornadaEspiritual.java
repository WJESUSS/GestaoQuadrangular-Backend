package com.gestaoigrejaemcelula.demo.domain.entity;



import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Histórico incremental de decisões espirituais de um visitante.
 * Cada linha representa UMA etapa conquistada (Aceitou Jesus, Batismo, Reconciliação).
 * A uniqueConstraint impede registrar a mesma decisão duas vezes para o mesmo visitante.
 */
@Entity
@Table(
        name = "jornada_espiritual",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_jornada_visitante_decisao",
                columnNames = {"visitante_id", "decisao"}
        )
)
public class JornadaEspiritual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "visitante_id", nullable = false)
    private Visitante visitante;

    @Enumerated(EnumType.STRING)
    @Column(name = "decisao", nullable = false, length = 30)
    private DecisaoEspiritual decisao;

    @Column(nullable = false)
    private LocalDate dataRegistro;

    /** Quem registrou a decisão (nome do líder/secretário logado) */
    @Column(length = 100)
    private String registradoPor;

    /** Observação livre: "culto de domingo", "Casa de Paz Flores" etc. */
    @Column(length = 255)
    private String observacao;

    // ── getters / setters ────────────────────────────────────────

    public Long getId() { return id; }

    public Visitante getVisitante() { return visitante; }
    public void setVisitante(Visitante visitante) { this.visitante = visitante; }

    public DecisaoEspiritual getDecisao() { return decisao; }
    public void setDecisao(DecisaoEspiritual decisao) { this.decisao = decisao; }

    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }

    public String getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(String registradoPor) { this.registradoPor = registradoPor; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
