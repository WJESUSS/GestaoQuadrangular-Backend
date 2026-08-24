package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Convertido;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusConvertido;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConvertidoResponseDTO {

    private Long id;
    private StatusConvertido status;
    private String statusDescricao;
    private LocalDateTime dataRegistro;

    // Quem registrou / autorizou
    private String registradoPorNome;
    private String autorizadoPorNome;
    private LocalDateTime dataAutorizacao;

    // Resultado
    private LocalDate dataBatismoIgreja;
    private Long membroCriadoId;

    // Dados da ficha
    private String nome;
    private String telefone;
    private String email;
    private LocalDate dataConversao;

    public ConvertidoResponseDTO() {}

    public ConvertidoResponseDTO(Convertido c) {
        this.id = c.getId();
        this.status = c.getStatus();
        this.statusDescricao = c.getStatus().getDescricao();
        this.dataRegistro = c.getDataRegistro();

        if (c.getRegistradoPor() != null) {
            this.registradoPorNome = c.getRegistradoPor().getNome();
        }
        if (c.getAutorizadoPor() != null) {
            this.autorizadoPorNome = c.getAutorizadoPor().getNome();
        }

        this.dataAutorizacao = c.getDataAutorizacao();
        this.dataBatismoIgreja = c.getDataBatismoIgreja();
        this.membroCriadoId = c.getMembroCriadoId();

        this.nome = c.getNome();
        this.telefone = c.getTelefone();
        this.email = c.getEmail();
        this.dataConversao = c.getDataConversao();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatusConvertido getStatus() { return status; }
    public void setStatus(StatusConvertido status) { this.status = status; }

    public String getStatusDescricao() { return statusDescricao; }
    public void setStatusDescricao(String statusDescricao) { this.statusDescricao = statusDescricao; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }

    public String getRegistradoPorNome() { return registradoPorNome; }
    public void setRegistradoPorNome(String registradoPorNome) { this.registradoPorNome = registradoPorNome; }

    public String getAutorizadoPorNome() { return autorizadoPorNome; }
    public void setAutorizadoPorNome(String autorizadoPorNome) { this.autorizadoPorNome = autorizadoPorNome; }

    public LocalDateTime getDataAutorizacao() { return dataAutorizacao; }
    public void setDataAutorizacao(LocalDateTime dataAutorizacao) { this.dataAutorizacao = dataAutorizacao; }

    public LocalDate getDataBatismoIgreja() { return dataBatismoIgreja; }
    public void setDataBatismoIgreja(LocalDate dataBatismoIgreja) { this.dataBatismoIgreja = dataBatismoIgreja; }

    public Long getMembroCriadoId() { return membroCriadoId; }
    public void setMembroCriadoId(Long membroCriadoId) { this.membroCriadoId = membroCriadoId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDataConversao() { return dataConversao; }
    public void setDataConversao(LocalDate dataConversao) { this.dataConversao = dataConversao; }
}
