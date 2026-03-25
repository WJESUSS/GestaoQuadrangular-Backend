package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.RankingCelulaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RankingCelulaProjection;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingCelulaService {

    private final CelulaRepository celulaRepository;

    private static final DateTimeFormatter MES_ANO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Gera o ranking das células para um mês específico.
     * A pontuação é calculada APENAS com base nos dados do mês informado.
     * Quando muda o mês, o ranking "zera" naturalmente, pois usa apenas os dados daquele período.
     *
     * @param mesAno String no formato "YYYY-MM" (ex: "2026-03")
     * @return Lista ordenada de RankingCelulaDTO com posições e pontuações
     */
    public List<RankingCelulaDTO> gerarRanking(String mesAno) {
        // Validação do formato YYYY-MM
        if (mesAno == null || mesAno.trim().isEmpty()) {
            mesAno = YearMonth.now().format(MES_ANO_FORMATTER); // Usa mês atual se não informado
        }

        try {
            YearMonth.parse(mesAno, MES_ANO_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de mês inválido. Use YYYY-MM (ex: 2026-03)");
        }

        // Busca os dados brutos APENAS do mês informado
        List<RankingCelulaProjection> dadosBrutos = celulaRepository.buscarDadosRankingNativo(mesAno);

        if (dadosBrutos.isEmpty()) {
            return List.of(); // Retorna vazio se não houver dados no mês
        }

        // Converte e calcula pontuação (baseada apenas no mês)
        List<RankingCelulaDTO> listaRanking = dadosBrutos.stream()
                .map(RankingCelulaDTO::new)
                .peek(this::calcularPontuacaoManual)
                .collect(Collectors.toList());

        // Ordena por pontuação descendente
        List<RankingCelulaDTO> ordenado = listaRanking.stream()
                .sorted(Comparator.comparingInt(RankingCelulaDTO::getPontuacao).reversed())
                .toList();

        // Atribui posições (1º, 2º, ...)
        for (int i = 0; i < ordenado.size(); i++) {
            ordenado.get(i).setPosicao(i + 1);
        }

        return ordenado;
    }

    /**
     * Calcula a pontuação com base apenas nos indicadores do mês.
     * - Presença média × 4
     * - Visitantes × 3
     * - Consolidados × 3
     * - Batismos × 5
     * - Multiplicou? +20 pontos
     */
    private void calcularPontuacaoManual(RankingCelulaDTO dto) {
        int pontos = 0;

        pontos += (dto.getPresencaMedia() != null ? dto.getPresencaMedia() : 0) * 4;
        pontos += (dto.getVisitantes() != null ? dto.getVisitantes() : 0) * 3;
        pontos += (dto.getConsolidados() != null ? dto.getConsolidados() : 0) * 3;
        pontos += (dto.getBatismos() != null ? dto.getBatismos() : 0) * 5;
        pontos += Boolean.TRUE.equals(dto.getMultiplicou()) ? 20 : 0;

        dto.setPontuacao(pontos);
    }

    /**
     * Método conveniente para gerar ranking do mês atual
     */
    public List<RankingCelulaDTO> gerarRankingMesAtual() {
        String mesAtual = YearMonth.now().format(MES_ANO_FORMATTER);
        return gerarRanking(mesAtual);
    }
}