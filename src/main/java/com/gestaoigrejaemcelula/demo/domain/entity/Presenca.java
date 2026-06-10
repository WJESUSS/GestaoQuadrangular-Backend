package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.JustificativaFalta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "presencas")
public class Presenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membro_id")
    private Membro membro;

    private LocalDate data;

    private boolean presente;
    private String status;
    private String tipoEvento;

    @ManyToOne
    @JoinColumn(name = "relatorio_id")
    private Relatorio relatorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "justificativa_falta")
    private JustificativaFalta justificativaFalta;
}