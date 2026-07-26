package com.gestaoigrejaemcelula.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import jakarta.persistence.*; // observe que Spring Boot 3 usa Jakarta, não javax
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity // <<< ESSENCIAL

@Table(name = "usuarios")
// opcional, mas recomendado
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // chave primária auto increment
    private Long id;
    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;
    @Column(nullable = false, unique = true)
    private String email;

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;
    private String nome;

    @Column(name = "email_pendente")
    private String emailPendente;

    /** Nova senha (já com hash BCrypt) aguardando aprovação do admin. Null = sem solicitação. */
    @Column(name = "senha_pendente")
    private String senhaPendente;

    // Getters e setters
    public String getEmailPendente()            { return emailPendente; }
    public void   setEmailPendente(String v)    { this.emailPendente = v; }

    public String getSenhaPendente()            { return senhaPendente; }
    public void   setSenhaPendente(String v)    { this.senhaPendente = v; }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    private boolean ativo;

    // getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // UserDetails methods
// Adicione este import

// ... dentro da classe Usuario

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 💡 Remova o "ROLE_" para bater com o que está no seu banco e no seu SecurityConfig
        return List.of(new SimpleGrantedAuthority(perfil.name()));
    }

    @Override
    public String getPassword() {
        return getSenha();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }


    public Boolean getAtivo() {
        return isAtivo();
    }

    public Celula getCelula() {
        return celula;
    }

    public void setCelula(Celula celula) {
        this.celula = celula;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "celula_id")
    private Celula celula;

    @Column(name = "telefone_whatsapp", length = 20)
    private String telefoneWhatsapp;

    public String getTelefoneWhatsapp() {
        return telefoneWhatsapp;
    }

    public void setTelefoneWhatsapp(String telefoneWhatsapp) {
        this.telefoneWhatsapp = telefoneWhatsapp;
    }

}
