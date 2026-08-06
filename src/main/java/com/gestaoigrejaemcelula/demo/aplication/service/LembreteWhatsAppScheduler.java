package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.AniversarianteDTO;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LembreteWhatsAppScheduler {

    @Value("${whatsapp.api.template.aniversariantes:lembrete_aniversariantes}")
    private String templateAniversariantes;

    private final UsuarioRepository usuarioRepository;
    private final WhatsAppService whatsAppService;
    private final AniversarioService aniversarioService;

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

    @Scheduled(cron = "0 30 08 * * *", zone = "America/Sao_Paulo")
    public void lembrarAniversariantesDoDia() {
        List<AniversarianteDTO> aniversariantes = aniversarioService.listarAniversariantesDoDia();

        if (aniversariantes.isEmpty()) {
            log.info("Lembrete aniversariantes: nenhum aniversariante hoje");
            return;
        }

        List<Usuario> pastores = usuarioRepository
                .findByPerfilAndAtivoTrueAndTelefoneWhatsappIsNotNull(Perfil.PASTOR);

        log.info("_Lembrete aniversariantes: {} aniversariante(s), {} pastor(es) para notificar",
                aniversariantes.size(), pastores.size());

        String nomes = aniversariantes.stream()
                .map(dto -> "*" + dto.getNome() + "*")
                .collect(Collectors.joining(", "));

        for (Usuario pastor : pastores) {
            try {
                whatsAppService.enviarTemplate(
                        pastor.getTelefoneWhatsapp(),
                        templateAniversariantes,
                        "pt_BR",
                        pastor.getNome().split(" ")[0],
                        String.valueOf(aniversariantes.size()),
                        nomes
                );
            } catch (Exception e) {
                log.error("Erro ao notificar pastor {} ({}): {}", pastor.getId(), pastor.getNome(), e.getMessage(), e);
            }
        }

        log.info("Lembrete aniversariantes finalizado para {} pastores", pastores.size());
    }
}
