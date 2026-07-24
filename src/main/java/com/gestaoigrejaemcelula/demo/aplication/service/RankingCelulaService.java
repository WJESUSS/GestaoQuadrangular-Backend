package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.RankingCelulaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RankingCelulaProjection;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingCelulaService {

    private static final Logger log = LoggerFactory.getLogger(RankingCelulaService.class);

    private final CelulaRepository celulaRepository;
    private static final DateTimeFormatter MES_ANO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Cacheable(value = "ranking-celulas", key = "#mesAno")
    public List<RankingCelulaDTO> gerarRanking(String mesAno) {
        if (mesAno == null || mesAno.trim().isEmpty()) {
            mesAno = YearMonth.now().format(MES_ANO_FORMATTER);
        }

        try {
            YearMonth.parse(mesAno, MES_ANO_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de mês inválido. Use YYYY-MM (ex: 2026-03)");
        }

        List<RankingCelulaProjection> dadosBrutos = celulaRepository.buscarDadosRankingNativo(mesAno);

        if (dadosBrutos.isEmpty()) {
            return List.of();
        }

        List<RankingCelulaDTO> listaRanking = dadosBrutos.stream()
                .map(RankingCelulaDTO::new)
                .peek(this::calcularPontuacaoManual)
                .toList();

        List<RankingCelulaDTO> ordenado = listaRanking.stream()
                .sorted(Comparator.comparingInt(RankingCelulaDTO::getPontuacao).reversed())
                .toList();

        for (int i = 0; i < ordenado.size(); i++) {
            ordenado.get(i).setPosicao(i + 1);
        }

        return ordenado;
    }
    private void calcularPontuacaoManual(RankingCelulaDTO dto) {
        int pontos = 0;

        pontos += (dto.getPresencaMedia()  != null ? dto.getPresencaMedia()  : 0) * 5;
        pontos += (dto.getVisitantes()     != null ? dto.getVisitantes()     : 0) * 10;
        pontos += (dto.getConsolidados()   != null ? dto.getConsolidados()   : 0) * 3;
        pontos += (dto.getBatismos()       != null ? dto.getBatismos()       : 0) * 5;
        pontos += (dto.getAceitouJesus()   != null ? dto.getAceitouJesus()   : 0) * 15;
        pontos += (dto.getDesejaBatismo()  != null ? dto.getDesejaBatismo()  : 0) * 10;
        pontos += (dto.getReconciliou()    != null ? dto.getReconciliou()    : 0) * 8;
        pontos += Boolean.TRUE.equals(dto.getMultiplicou()) ? 20 : 0;

        // Log temporário para debug
        log.debug("=== {} ===", dto.getNomeCelula());
        log.debug("Presença: {}",      dto.getPresencaMedia());
        log.debug("Visitantes: {}",    dto.getVisitantes());
        log.debug("Aceitou Jesus: {}", dto.getAceitouJesus());
        log.debug("Deseja Batismo: {}", dto.getDesejaBatismo());
        log.debug("Reconciliou: {}",   dto.getReconciliou());
        log.debug("Batismos: {}",      dto.getBatismos());
        log.debug("Multiplicou: {}",   dto.getMultiplicou());
        log.debug("TOTAL: {}",         pontos);

        dto.setPontuacao(pontos);
    }

    @Cacheable(value = "ranking-celulas", key = "'mes-atual'")
    public List<RankingCelulaDTO> gerarRankingMesAtual() {
        String mesAtual = YearMonth.now().format(MES_ANO_FORMATTER);
        return gerarRanking(mesAtual);
    }
    @CacheEvict(value = "ranking-celulas", allEntries = true)
    public void limparCache() {
        // Método intencionalmente vazio: a limpeza do cache é tratada pela anotação @CacheEvict.
    }


}
