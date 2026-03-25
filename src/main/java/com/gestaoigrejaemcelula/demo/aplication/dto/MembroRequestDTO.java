package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import java.time.LocalDate;

public class MembroRequestDTO {

    private String nome;
    private String telefone;
    private String email;
    private String endereco; // Faltava este!
    private LocalDate dataNascimento; // Faltava este!
    private LocalDate dataConversao;  // Faltava este!
    private LocalDate dataBatismo;    // Faltava este!
    private StatusMembro status;private String cpf;
    private EstadoCivil estadoCivil;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

// --- GETTERS E SETTERS (MUITO IMPORTANTE) ---

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public LocalDate getDataConversao() { return dataConversao; }
    public void setDataConversao(LocalDate dataConversao) { this.dataConversao = dataConversao; }

    public LocalDate getDataBatismo() { return dataBatismo; }
    public void setDataBatismo(LocalDate dataBatismo) { this.dataBatismo = dataBatismo; }

    // Mantenha os outros getters/setters que você já tinha abaixo...
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public StatusMembro getStatus() { return status; }
    public void setStatus(StatusMembro status) { this.status = status; }
}