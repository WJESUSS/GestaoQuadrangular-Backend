package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.EncontroMissao70;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class EncontroMissao70ResponseDTO {

    private Long id;
    private LocalDate dataEncontro;
    private LocalTime horaEncontro;
    private int numeroSemana;
    private String observacoes;
    private List<PresenteDTO> visitantesPresentes;

    public static class PresenteDTO {
        private Long id;
        private String nome;
        public PresenteDTO(Long id, String nome) { this.id = id; this.nome = nome; }
        public Long getId() { return id; }
        public String getNome() { return nome; }
    }

    public static EncontroMissao70ResponseDTO de(EncontroMissao70 encontro) {
        EncontroMissao70ResponseDTO dto = new EncontroMissao70ResponseDTO();
        dto.id = encontro.getId();
        dto.dataEncontro = encontro.getDataEncontro();
        dto.horaEncontro = encontro.getHoraEncontro();
        dto.numeroSemana = encontro.getNumeroSemana();
        dto.observacoes = encontro.getObservacoes();
        dto.visitantesPresentes = encontro.getVisitantesPresentes() != null
                ? encontro.getVisitantesPresentes().stream()
                .map(v -> new PresenteDTO(v.getId(), v.getNome()))
                .collect(Collectors.toList())
                : List.of();
        return dto;
    }

    public Long getId() { return id; }
    public LocalDate getDataEncontro() { return dataEncontro; }
    public LocalTime getHoraEncontro() { return horaEncontro; }
    public int getNumeroSemana() { return numeroSemana; }
    public String getObservacoes() { return observacoes; }
    public List<PresenteDTO> getVisitantesPresentes() { return visitantesPresentes; }
}
