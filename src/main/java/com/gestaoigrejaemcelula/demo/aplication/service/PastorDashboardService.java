package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.PastorMetricasDTO;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoAcompanhamentoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Cache por mês — chave "2026-05", "2026-04", etc.
    @Cacheable(value = "metricas-pastor", key = "#mes")
    @Transactional(readOnly = true)
    public PastorMetricasDTO carregarMetricas(String mes) {
        YearMonth yearMonth = YearMonth.parse(mes);
        int mesInt = yearMonth.getMonthValue();
        int anoInt = yearMonth.getYear();

        LocalDate inicio = yearMonth.atDay(1);
        LocalDate fim    = yearMonth.atEndOfMonth();

        Long totalMembros     = membroRepository.count();
        Long novosMembrosMes  = membroRepository.novosMembrosMes(inicio, fim);
        Long naoAcompanhados  = acompanhamentoRepository.contarPendentesReal(mesInt, anoInt, mes);
        Long celulasAtivas    = celulaRepository.countByAtivaTrue();

        return new PastorMetricasDTO(totalMembros, novosMembrosMes, naoAcompanhados, celulasAtivas);
    }
}