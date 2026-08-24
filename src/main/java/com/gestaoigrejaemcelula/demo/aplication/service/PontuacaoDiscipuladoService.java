package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.repository.AcompanhamentoDiscipuladoColetivoParticipanteRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.AcompanhamentoDiscipuladoIndividualRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PontuacaoDiscipuladoService {

    public static final int PONTOS_POR_DISCIPULADO = 5;

    private final AcompanhamentoDiscipuladoIndividualRepository individualRepository;
    private final AcompanhamentoDiscipuladoColetivoParticipanteRepository participanteRepository;
    private final DiscipuladoRelatorioRepository discipuladoRelatorioRepository;

    public int calcularPontos(int unidades) {
        if (unidades <= 0) {
            return 0;
        }
        return unidades * PONTOS_POR_DISCIPULADO;
    }

    public Map<Long, Integer> unidadesDiscipuladoPorCelulaNoMes(String mesAno) {
        Map<Long, Integer> unidadesPorCelula = new HashMap<>();

        for (Object[] linha : individualRepository.contarUnidadesPorCelulaNoMes(mesAno)) {
            Long celulaId = ((Number) linha[0]).longValue();
            int unidades = ((Number) linha[1]).intValue();
            unidadesPorCelula.merge(celulaId, unidades, Integer::sum);
        }

        for (Object[] linha : participanteRepository.contarUnidadesPorCelulaNoMes(mesAno)) {
            Long celulaId = ((Number) linha[0]).longValue();
            int unidades = ((Number) linha[1]).intValue();
            unidadesPorCelula.merge(celulaId, unidades, Integer::sum);
        }

        return unidadesPorCelula;
    }

    public Map<Long, Integer> pontosDiscipuladoPorCelulaNoMes(String mesAno) {
        Map<Long, Integer> pontosPorCelula = new HashMap<>();
        unidadesDiscipuladoPorCelulaNoMes(mesAno).forEach((celulaId, unidades) ->
                pontosPorCelula.put(celulaId, calcularPontos(unidades)));
        return pontosPorCelula;
    }

    /**
     * Pontos de cultos/escola bíblica (relatório semanal de discipulado) por célula no mês.
     * quarta=2 · quinta=2 · domingo manhã/noite=4 cada · escola bíblica=5.
     */
    public Map<Long, Integer> pontosCultosPorCelulaNoMes(String mesAno) {
        String[] partes = mesAno.split("-");
        int ano = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);

        Map<Long, Integer> pontosPorCelula = new HashMap<>();
        for (Object[] linha : discipuladoRelatorioRepository.somarPontosCultosPorCelulaNoMes(mes, ano)) {
            Long celulaId = ((Number) linha[0]).longValue();
            int pontos = ((Number) linha[1]).intValue();
            pontosPorCelula.merge(celulaId, pontos, Integer::sum);
        }
        return pontosPorCelula;
    }
}
