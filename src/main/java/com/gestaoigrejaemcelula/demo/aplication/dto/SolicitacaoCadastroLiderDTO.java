package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO usado pelo líder para se cadastrar publicamente.
 * Não inclui "perfil" nem "ativo" — esses são definidos
 * automaticamente pelo sistema (LIDER_CELULA / ativo=false).
 */
public class SolicitacaoCadastroLiderDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    // Opcional: líder pode informar a qual célula pertence
    private Long celulaId;

    // ── Getters / Setters ──────────────────────────────────────────────────

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }
}