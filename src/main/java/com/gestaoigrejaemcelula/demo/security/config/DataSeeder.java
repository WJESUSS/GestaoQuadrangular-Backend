package com.gestaoigrejaemcelula.demo.security.config;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String emailAdmin = "admin@gmail.com";

        // verifica se já existe
        if (usuarioRepository.findByEmailIgnoreCase(emailAdmin).isEmpty()) {

            Usuario admin = new Usuario();
            admin.setEmail(emailAdmin.toLowerCase());
            admin.setSenha(passwordEncoder.encode("123456"));
            admin.setPerfil(Perfil.ADMIN);
            admin.setNome("Administrador");
            admin.setAtivo(true);

            usuarioRepository.save(admin);

            System.out.println("✅ ADMIN CRIADO COM SUCESSO!");
        } else {
            System.out.println("ℹ️ ADMIN JÁ EXISTE.");
        }
    }
}