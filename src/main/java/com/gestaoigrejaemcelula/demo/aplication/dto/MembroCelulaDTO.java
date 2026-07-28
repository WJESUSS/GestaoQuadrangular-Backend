package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.CargoMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.Tipo;

import java.util.Set;

public class MembroCelulaDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String status;
    private Set<CargoMembro> cargos;
    private Tipo tipo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Set<CargoMembro> getCargos() { return cargos; }
    public void setCargos(Set<CargoMembro> cargos) { this.cargos = cargos; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
}
