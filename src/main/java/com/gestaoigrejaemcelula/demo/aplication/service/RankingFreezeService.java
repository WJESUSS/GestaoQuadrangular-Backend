package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.RankingCelulaDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.RankingFinalizado;
import com.gestaoigrejaemcelula.demo.domain.repository.RankingFinalizadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingFreezeService {

    private final RankingFinalizadoRepository rankingFinalizadoRepository;
    private final RankingCelulaService rankingCelulaService;
    private static final DateTimeFormatter MES_ANO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public boolean isMesFinalizado(String mesAno) {
        YearMonth mes = YearMonth.parse(mesAno, MES_ANO_FORMATTER);
        YearMonth atual = YearMonth.now();
        return mes.isBefore(atual);
    }

    @Transactional(readOnly = true)
    public List<RankingCelulaDTO> buscarRankingFinalizado(String mesAno) {
        List<RankingFinalizado> finalizados = rankingFinalizadoRepository.findByMesAnoOrderByPosicaoAsc(mesAno);

        return finalizados.stream()
                .map(rf -> {
                    RankingCelulaDTO dto = new RankingCelulaDTO();
                    dto.setCelulaId(rf.getCelulaId());
                    dto.setNomeCelula(rf.getNomeCelula());
                    dto.setLider(rf.getLider());
                    dto.setPresencaMedia(rf.getPresencaMedia());
                    dto.setVisitantes(rf.getVisitantes());
                    dto.setConsolidados(rf.getConsolidados());
                    dto.setBatismos(rf.getBatismos());
                    dto.setMultiplicou(rf.getMultiplicou());
                    dto.setAceitouJesus(rf.getAceitouJesus());
                    dto.setDesejaBatismo(rf.getDesejaBatismo());
                    dto.setReconciliou(rf.getReconciliou());
                    dto.setPontosDiscipulado(rf.getPontosDiscipulado());
                    dto.setQuantidadeDiscipulados(rf.getQuantidadeDiscipulados());
                    dto.setPontosCultos(rf.getPontosCultos());
                    dto.setPontuacao(rf.getPontuacao());
                    dto.setPosicao(rf.getPosicao());
                    return dto;
                })
                .toList();
    }

    @Transactional
    public int congelarMes(String mesAno) {
        if (rankingFinalizadoRepository.existsByMesAno(mesAno)) {
            log.info("Mês {} já foi finalizado anteriormente, ignorando", mesAno);
            return 0;
        }

        List<RankingCelulaDTO> ranking = rankingCelulaService.gerarRanking(mesAno);

        if (ranking.isEmpty()) {
            log.info("Nenhum dado encontrado para o mês {}, nada a finalizar", mesAno);
            return 0;
        }

        LocalDateTime agora = LocalDateTime.now();

        List<RankingFinalizado> finais = ranking.stream().map(dto -> {
            RankingFinalizado rf = new RankingFinalizado();
            rf.setMesAno(mesAno);
            rf.setCelulaId(dto.getCelulaId());
            rf.setNomeCelula(dto.getNomeCelula());
            rf.setLider(dto.getLider());
            rf.setPresencaMedia(dto.getPresencaMedia());
            rf.setVisitantes(dto.getVisitantes());
            rf.setConsolidados(dto.getConsolidados());
            rf.setBatismos(dto.getBatismos());
            rf.setMultiplicou(dto.getMultiplicou());
            rf.setAceitouJesus(dto.getAceitouJesus());
            rf.setDesejaBatismo(dto.getDesejaBatismo());
            rf.setReconciliou(dto.getReconciliou());
            rf.setPontosDiscipulado(dto.getPontosDiscipulado());
            rf.setQuantidadeDiscipulados(dto.getQuantidadeDiscipulados());
            rf.setPontosCultos(dto.getPontosCultos());
            rf.setPontuacao(dto.getPontuacao());
            rf.setPosicao(dto.getPosicao());
            rf.setDataFinalizacao(agora);
            return rf;
        }).toList();

        rankingFinalizadoRepository.saveAll(finais);

        log.info("Mês {} finalizado com {} células no ranking", mesAno, finais.size());
        return finais.size();
    }
}
