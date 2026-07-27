package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InatividadeScheduler {

    private static final int DIAS_INATIVIDADE = 14;

    private final UsuarioRepository usuarioRepository;

    @Scheduled(cron = "0 17 23 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void suspenderInativos() {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(DIAS_INATIVIDADE);
        List<Usuario> inativos = usuarioRepository.findInativos(dataLimite, Perfil.ADMIN);

        if (inativos.isEmpty()) {
            log.info("Verificação de inatividade: nenhum usuário inativo encontrado");
            return;
        }

        log.info("Verificação de inatividade: {} usuário(s) será(ão) suspenso(s)", inativos.size());

        for (Usuario usuario : inativos) {
            try {
                usuario.setAtivo(false);
                usuarioRepository.save(usuario);
                log.info("Usuário suspenso por inatividade: {} (ID: {})", usuario.getEmail(), usuario.getId());
            } catch (Exception e) {
                log.error("Erro ao suspender usuário {} (ID: {}): {}", usuario.getEmail(), usuario.getId(), e.getMessage(), e);
            }
        }

        log.info("Verificação de inatividade finalizada: {} usuário(s) suspenso(s)", inativos.size());
    }
}
