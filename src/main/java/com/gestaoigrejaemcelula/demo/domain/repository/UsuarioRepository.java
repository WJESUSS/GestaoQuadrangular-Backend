package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);

    List<Usuario> findByPerfilIn(List<Perfil> pastor);

    @Query("""
        SELECT u FROM Usuario u
        WHERE u.perfil = :perfil AND u.ativo = true AND u.telefoneWhatsapp IS NOT NULL
    """)
    List<Usuario> findByPerfilAndAtivoTrueAndTelefoneWhatsappIsNotNull(Perfil perfil);

}
