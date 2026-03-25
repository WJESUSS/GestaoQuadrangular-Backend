package com.gestaoigrejaemcelula.demo.aplication.dto;


import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import lombok.Getter;
@Getter
public class CadastroUsuarioDTO {
    private String nome;
    private String email;
    private String senha;
    private Perfil perfil;
    private boolean ativo;
    private Long celulaId;

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getNome() {
        return nome;
    }

    public Long getCelulaId() {
        return celulaId;
    }

    public void setCelulaId(Long celulaId) {
        this.celulaId = celulaId;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
