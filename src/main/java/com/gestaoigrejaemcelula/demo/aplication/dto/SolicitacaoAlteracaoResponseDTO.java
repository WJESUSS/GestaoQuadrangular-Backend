package com.gestaoigrejaemcelula.demo.aplication.dto;

public class SolicitacaoAlteracaoResponseDTO {

    private Long    id;
    private String  nome;
    private String  emailAtual;
    private String  emailPendente;
    private boolean senhaPendente;
    private String  mensagem;

    public SolicitacaoAlteracaoResponseDTO(Long id, String nome, String emailAtual,
                                           String emailPendente, boolean senhaPendente,
                                           String mensagem) {
        this.id            = id;
        this.nome          = nome;
        this.emailAtual    = emailAtual;
        this.emailPendente = emailPendente;
        this.senhaPendente = senhaPendente;
        this.mensagem      = mensagem;
    }

    public Long    getId()            { return id; }
    public String  getNome()          { return nome; }
    public String  getEmailAtual()    { return emailAtual; }
    public String  getEmailPendente() { return emailPendente; }
    public boolean isSenhaPendente()  { return senhaPendente; }
    public String  getMensagem()      { return mensagem; }
}