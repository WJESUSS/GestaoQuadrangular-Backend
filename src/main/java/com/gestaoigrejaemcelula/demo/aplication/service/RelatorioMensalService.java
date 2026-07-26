package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Notificacao;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoRelatorioRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.NotificacaoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.RelatorioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioMensalService {

    private final RelatorioRepository relatorioRepository;
    private final DiscipuladoRelatorioRepository discipuladoRepository;
    private final CelulaRepository celulaRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final NotificacaoService notificacaoService;

    private static final int SEMANAS_ESPERADAS_NO_MES = 4;

    @Transactional
    public void verificarEEnviarParabens(Long celulaId) {
        YearMonth mesAtual = YearMonth.now();
        LocalDate inicioMes = mesAtual.atDay(1);
        LocalDate fimMes = mesAtual.atEndOfMonth();

        long relatoriosCelula = relatorioRepository
                .countByCelulaIdAndDataReuniaoBetween(celulaId, inicioMes, fimMes);

        long semanasDiscipulado = discipuladoRepository
                .countSemanasByCelulaIdAndPeriodo(celulaId, inicioMes, fimMes);

        log.info("Verificação mensal célula {}: relatórios={}/4, discipulado={}/4",
                celulaId, relatoriosCelula, semanasDiscipulado);

        if (relatoriosCelula >= SEMANAS_ESPERADAS_NO_MES
                && semanasDiscipulado >= SEMANAS_ESPERADAS_NO_MES) {

            Celula celula = celulaRepository.findById(celulaId).orElse(null);
            if (celula == null || celula.getLider() == null) return;

            Usuario lider = celula.getLider();

            boolean jaEnviadoEsteMes = notificacaoRepository
                    .existsByUsuarioIdAndTipoAndDataEnvioAfter(
                            lider.getId(),
                            Notificacao.TipoNotificacao.RELATORIO_MENSAL_COMPLETO,
                            inicioMes.atStartOfDay());

            if (jaEnviadoEsteMes) {
                log.info("Parabéns já enviado este mês para líder {} (célula {})",
                        lider.getNome(), celula.getNome());
                return;
            }

            String titulo = "Parabéns! 🎉";
            String mensagem = String.format(
                    "Parabéns, %s! Você entregou todos os relatórios de célula e discipulado do mês de %s. " +
                    "Continue assim, seu trabalho faz a diferença!",
                    lider.getNome().split(" ")[0],
                    mesAtual.getMonth().getDisplayName(java.time.format.TextStyle.FULL,
                            new java.util.Locale("pt", "BR"))
            );

            notificacaoService.enviarNotificacao(
                    lider.getId(),
                    titulo,
                    mensagem,
                    Notificacao.TipoNotificacao.RELATORIO_MENSAL_COMPLETO
            );

            notificacaoService.enviarWhatsAppParabens(lider);

            log.info("✅ Parabéns enviado para líder {} (célula {})",
                    lider.getNome(), celula.getNome());
        }
    }
}
