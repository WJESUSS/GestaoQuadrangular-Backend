package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * O líder preenche apenas o que quer alterar.
 * - Para trocar só a senha: envia senhaAtual + novaSenha (emailNovo fica null)
 * - Para trocar só o email: envia senhaAtual + emailNovo  (novaSenha fica null)
 * - Pode trocar os dois ao mesmo tempo.
 * A senhaAtual é sempre obrigatória para confirmar a identidade.
 */
public class SolicitacaoAlteracaoDTO {

    // Sempre obrigatório — confirma que é o dono da conta
    private String senhaAtual;

    // Opcional — novo e-mail desejado
    @Email(message = "E-mail inválido")
    private String emailNovo;

    // Opcional — nova senha desejada
    @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
    private String novaSenha;
    private String email;

    public String getEmail()         { return email; }
    public void   setEmail(String v) { this.email = v; }
    // Confirmação da nova senha (validada no service)
    private String confirmarNovaSenha;

    public String getSenhaAtual()          { return senhaAtual; }
    public void   setSenhaAtual(String v)  { this.senhaAtual = v; }

    public String getEmailNovo()           { return emailNovo; }
    public void   setEmailNovo(String v)   { this.emailNovo = v; }

    public String getNovaSenha()           { return novaSenha; }
    public void   setNovaSenha(String v)   { this.novaSenha = v; }

    public String getConfirmarNovaSenha()         { return confirmarNovaSenha; }
    public void   setConfirmarNovaSenha(String v) { this.confirmarNovaSenha = v; }

}