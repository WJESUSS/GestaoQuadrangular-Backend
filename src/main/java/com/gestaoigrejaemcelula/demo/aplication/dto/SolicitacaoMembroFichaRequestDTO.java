package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.CargoMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoArrolamento;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO enviado pelo Líder para solicitar cadastro de novo membro.
 * NÃO cadastra diretamente — cria uma solicitação PENDENTE para a Secretaria aprovar.
 */
@Getter
@Setter
public class SolicitacaoMembroFichaRequestDTO {

    // -------------------------------------------------------
    // DADOS BÁSICOS
    // -------------------------------------------------------

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String telefone;
    private String email;
    private String cpf;
    private String rg;

    private EstadoCivil estadoCivil;

    private LocalDate dataNascimento;
    private LocalDate dataConversao;
    private LocalDate dataBatismo;

    // -------------------------------------------------------
    // FILIAÇÃO E NATURALIDADE
    // -------------------------------------------------------

    private String nomeMae;
    private String nomePai;
    private String nomeConjuge;
    private String naturalidade;

    // -------------------------------------------------------
    // ESCOLARIDADE E PROFISSÃO
    // -------------------------------------------------------

    private String grauEscolaridade;
    private String curso;
    private String profissao;

    // -------------------------------------------------------
    // ENDEREÇO
    // -------------------------------------------------------

    private String endereco;
    private String numero;
    private String bairro;
    private String cidade;
    private String cep;
    private String uf;

    // -------------------------------------------------------
    // DADOS ESPIRITUAIS
    // -------------------------------------------------------

    private Boolean pertenceOutraReligiao;
    private String qualReligiao;

    private Boolean batizadoNasAguas;
    private LocalDate dataBatizadoNasAguas;
    private String igrejaBatizadoNasAguas;

    private Boolean batizadoEspiritoSanto;

    // -------------------------------------------------------
    // ARROLAMENTO
    // -------------------------------------------------------

    private TipoArrolamento tipoArrolamento;
    private String jurisdicaoArrolamento;
    private String arroladoPor;

    // -------------------------------------------------------
    // CARGOS
    // -------------------------------------------------------

    private Set<CargoMembro> cargos;

    // -------------------------------------------------------
    // OUTROS
    // -------------------------------------------------------

    private String observacoes;

    // -------------------------------------------------------
    // GETTERS E SETTERS
    // ---------------------------------------------------
}