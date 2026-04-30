package com.gestaoigrejaemcelula.demo.aplication.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // ESSENCIAL para o Jackson conseguir criar o JSON
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertaDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String nomeCelula;
    private Integer totalFaltas;

    public AlertaDTO(Long id, String nome, String telefone, String nomeCelula, Long totalFaltas) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.nomeCelula = nomeCelula;
        this.totalFaltas = totalFaltas.intValue(); // Converte aqui
    }
}
