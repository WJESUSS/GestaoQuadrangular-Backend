package com.gestaoigrejaemcelula.demo.domain.entity;

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

    // Se true = presente, se false = falta
    private boolean presente;
    private String status;
    // Opcional: para saber se foi Culto de Domingo ou Célula
    private String tipoEvento;
}