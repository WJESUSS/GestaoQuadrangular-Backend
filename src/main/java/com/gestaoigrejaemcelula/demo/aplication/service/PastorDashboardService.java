package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.PastorMetricasDTO;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoAcompanhamentoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class PastorDashboardService {

    @Autowired
    private MembroRepository membroRepository;
    @Autowired
    private CelulaRepository celulaRepository;

    @Autowired
    private DiscipuladoAcompanhamentoRepository acompanhamentoRepository;
    public PastorMetricasDTO carregarMetricas(String mes) {
        // 🔹 Converte "2026-01" para YearMonth para extrair Mês e Ano
        YearMonth yearMonth = YearMonth.parse(mes);
        int mesInt = yearMonth.getMonthValue();
        int anoInt = yearMonth.getYear();

        LocalDate inicio = yearMonth.atDay(1);
        LocalDate fim = yearMonth.atEndOfMonth();

        // Busca o total de membros
        Long totalMembros = membroRepository.count();

        // Busca novos membros no intervalo do mês
        Long novosMembrosMes = membroRepository.novosMembrosMes(inicio, fim);

        // Busca pendências de acompanhamento
        Long naoAcompanhados = acompanhamentoRepository.contarPendentesReal(
                mesInt,
                anoInt,
                mes
        );

        // --- NOVA LINHA: Busca o total de células que estão com status ativa = true ---
        Long celulasAtivas = celulaRepository.countByAtivaTrue();

        // Retorna o DTO com o novo campo (certifique-se que o DTO tem esse 4º parâmetro no construtor)
        return new PastorMetricasDTO(
                totalMembros,
                novosMembrosMes,
                naoAcompanhados,
                celulasAtivas
        );
    }
}