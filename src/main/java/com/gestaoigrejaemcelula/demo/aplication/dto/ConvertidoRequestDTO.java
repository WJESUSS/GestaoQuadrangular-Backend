package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoArrolamento;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Ficha de Convertido enviada pela Secretaria.
 * Pessoa que aceitou Jesus mas ainda NÃO é membro — só se torna membro
 * após o batismo nesta igreja e a autorização da Secretaria.
 */
@Getter
@Setter
public class ConvertidoRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String telefone;
    private String email;
    private String cpf;
    private String rg;

    private EstadoCivil estadoCivil;

    private LocalDate dataNascimento;
    private LocalDate dataConversao;

    private String nomeMae;
    private String nomePai;
    private String nomeConjuge;
    private String naturalidade;

    private String grauEscolaridade;
    private String curso;
    private String profissao;

    private String endereco;
    private String numero;
    private String bairro;
    private String cidade;
    private String cep;
    private String uf;

    private Boolean pertenceOutraReligiao;
    private String qualReligiao;

    /** Batismo anterior em outra igreja */
    private Boolean batizadoNasAguas;
    private LocalDate dataBatizadoNasAguas;
    private String igrejaBatizadoNasAguas;

    private Boolean batizadoEspiritoSanto;

    private TipoArrolamento tipoArrolamento;
    private String jurisdicaoArrolamento;
    private String arroladoPor;

    private String observacoes;
}
