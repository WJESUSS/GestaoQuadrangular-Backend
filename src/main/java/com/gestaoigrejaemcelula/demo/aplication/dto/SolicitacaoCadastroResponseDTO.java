package com.gestaoigrejaemcelula.demo.aplication.dto;

/**
 * Retornado após o líder se cadastrar com sucesso (pendente de aprovação).
 */
public class SolicitacaoCadastroResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String perfil;
    private boolean ativo;
    private String mensagem;

    public SolicitacaoCadastroResponseDTO() {}

    public SolicitacaoCadastroResponseDTO(Long id, String nome, String email,
                                          String perfil, boolean ativo, String mensagem) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.ativo = ativo;
        this.mensagem = mensagem;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}