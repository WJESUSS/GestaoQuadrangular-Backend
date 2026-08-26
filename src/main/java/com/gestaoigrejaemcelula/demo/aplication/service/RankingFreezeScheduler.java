package com.gestaoigrejaemcelula.demo.aplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingFreezeScheduler {

    private final RankingFreezeService rankingFreezeService;
    private static final DateTimeFormatter MES_ANO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Scheduled(cron = "0 55 23 L * *", zone = "America/Sao_Paulo")
    public void finalizarMes() {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        String mesAno = mesAnterior.format(MES_ANO_FORMATTER);

        log.info("Iniciando finalização do ranking do mês: {}", mesAno);

        try {
            int quantidade = rankingFreezeService.congelarMes(mesAno);
            log.info("Finalização do mês {} concluída: {} células no ranking", mesAno, quantidade);
        } catch (Exception e) {
            log.error("Erro ao finalizar ranking do mês {}: {}", mesAno, e.getMessage(), e);
        }
    }
}
