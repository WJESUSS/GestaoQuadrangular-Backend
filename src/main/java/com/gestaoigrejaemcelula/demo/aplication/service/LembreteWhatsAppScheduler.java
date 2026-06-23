package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LembreteWhatsAppScheduler {

    private final UsuarioRepository usuarioRepository;
    private final WhatsAppService whatsAppService;

    @Scheduled(cron = "0 0 21 * * TUE", zone = "America/Sao_Paulo")
    public void lembrarRelatorioCelula() {
        List<Usuario> lideres = usuarioRepository
                .findByPerfilAndAtivoTrueAndTelefoneWhatsappIsNotNull(Perfil.LIDER_CELULA);

        log.info("Lembrete relatório célula: {} líderes encontrados para notificar", lideres.size());

        for (Usuario lider : lideres) {
            try {
                String primeiroNome = lider.getNome().split(" ")[0];
                whatsAppService.enviarTemplate(
                        lider.getTelefoneWhatsapp(),
                        "lembrete_relatorio_celula",
                        "pt_BR",
                        primeiroNome
                );
            } catch (Exception e) {
                log.error("Erro ao notificar líder {} ({}): {}", lider.getId(), lider.getNome(), e.getMessage(), e);
            }
        }

        log.info("Lembrete relatório célula finalizado para {} líderes", lideres.size());
    }

    @Scheduled(cron = "0 0 21 * * SAT", zone = "America/Sao_Paulo")
    public void lembrarRelatorioDiscipulado() {
        List<Usuario> lideres = usuarioRepository
                .findByPerfilAndAtivoTrueAndTelefoneWhatsappIsNotNull(Perfil.LIDER_CELULA);

        log.info("Lembrete relatório discipulado: {} líderes encontrados para notificar", lideres.size());

        for (Usuario lider : lideres) {
            try {
                String primeiroNome = lider.getNome().split(" ")[0];
                whatsAppService.enviarTemplate(
                        lider.getTelefoneWhatsapp(),
                        "lembrete_relatorio_discipulado",
                        "pt_BR",
                        primeiroNome
                );
            } catch (Exception e) {
                log.error("Erro ao notificar líder {} ({}): {}", lider.getId(), lider.getNome(), e.getMessage(), e);
            }
        }

        log.info("Lembrete relatório discipulado finalizado para {} líderes", lideres.size());
    }
}
