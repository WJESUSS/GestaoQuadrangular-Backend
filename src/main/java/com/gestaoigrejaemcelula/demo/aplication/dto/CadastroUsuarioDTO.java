package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CadastroUsuarioDTO {
    @NotBlank private String nome;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String senha;
    @NotNull private Perfil perfil;
    private boolean ativo;
    private Long celulaId;

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getNome() { return nome; }
    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public Perfil getPerfil() { return perfil; }
    public boolean isAtivo() { return ativo; }
}
