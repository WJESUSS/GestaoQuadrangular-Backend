package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusConvertido;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoArrolamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Ficha de Convertido: pessoa que aceitou Jesus mas ainda NÃO é membro.
 * Registrada pela Secretaria com os mesmos dados da ficha de membro.
 * Só se torna MEMBRO quando se batiza na igreja e a Secretaria autoriza
 * a membresia (autorizarMembresia), momento em que um Membro real é criado.
 */
@Getter
@Entity
@Setter
@Table(name = "convertidos", indexes = {
        @Index(name = "idx_convertido_status", columnList = "status")
})
public class Convertido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------------------------------------
    // CONTROLE DE FLUXO
    // -------------------------------------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConvertido status = StatusConvertido.AGUARDANDO_BATISMO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    /**
     * Secretário que registrou a ficha
     */
    @ManyToOne
    @JoinColumn(name = "registrado_por_id")
    private Usuario registradoPor;

    /**
     * Secretário que autorizou a membresia após o batismo
     */
    @ManyToOne
    @JoinColumn(name = "autorizado_por_id")
    private Usuario autorizadoPor;

    private LocalDateTime dataAutorizacao;

    /**
     * Data do batismo realizado nesta igreja (informada na autorização)
     */
    private LocalDate dataBatismoIgreja;

    /**
     * ID do membro criado após a autorização
     */
    private Long membroCriadoId;

    // -------------------------------------------------------
    // DADOS DA FICHA (mesmos campos da ficha de membro)
    // -------------------------------------------------------

    @Column(nullable = false)
    private String nome;

    private String telefone;
    private String email;

    @Column(length = 14)
    private String cpf;

    @Column(length = 20)
    private String rg;

    @Enumerated(EnumType.STRING)
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

    @Column(length = 9)
    private String cep;

    @Column(length = 2)
    private String uf;

    private Boolean pertenceOutraReligiao;
    private String qualReligiao;

    /**
     * Batismo anterior em OUTRA igreja (não substitui o batismo nesta igreja)
     */
    private Boolean batizadoNasAguas;
    private LocalDate dataBatizadoNasAguas;
    private String igrejaBatizadoNasAguas;

    private Boolean batizadoEspiritoSanto;

    @Enumerated(EnumType.STRING)
    private TipoArrolamento tipoArrolamento;

    private String jurisdicaoArrolamento;
    private String arroladoPor;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    // -------------------------------------------------------
    // LIFECYCLE
    // -------------------------------------------------------

    @PrePersist
    public void prePersist() {
        this.dataRegistro = LocalDateTime.now();
    }



}