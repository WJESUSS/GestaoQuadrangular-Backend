package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.AlertaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoRelatorioResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoAcompanhamento;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.AcompanhamentoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoRelatorioRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscipuladoService {

    private static final Logger log = LoggerFactory.getLogger(DiscipuladoService.class);

    @Autowired
    private DiscipuladoRelatorioRepository relatorioRepo;
    @Autowired
    private AcompanhamentoRepository accRepo;
    @Autowired
    private MembroRepository membroRepo;

    @Cacheable(value = "alertas-discipulado", key = "#mesRef")
    @Transactional(readOnly = true)
    public List<AlertaDTO> buscarAlertas(String mesRef) {
        int ano;
        int mes;

        try {
            if (mesRef != null && mesRef.contains("-")) {
                String[] partes = mesRef.split("-");
                ano    = Integer.parseInt(partes[0]);
                mes    = Integer.parseInt(partes[1]);
            } else {
                LocalDate hoje = LocalDate.now();
                ano    = hoje.getYear();
                mes    = hoje.getMonthValue();
                mesRef = String.format("%d-%02d", ano, mes);
            }

            List<Object[]> resultados = relatorioRepo.buscarAlertasPastor(mes, ano, mesRef);
            if (resultados == null) return new ArrayList<>();

            return resultados.stream().map(obj -> new AlertaDTO(
                    ((Number) obj[0]).longValue(),
                    (String)  obj[1],
                    (String)  obj[2],
                    (String)  obj[3],
                    ((Number) obj[4]).intValue()
            )).toList();

        } catch (Exception e) {
            log.error("Erro ao processar alertas", e);
            return new ArrayList<>();
        }
    }

    // Quando o pastor marca alguém como acompanhado,
    // invalida os caches relacionados para refletir na próxima consulta
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "alertas-discipulado", key = "#mesRef"),
            @CacheEvict(value = "metricas-pastor",     key = "#mesRef")
    })
    public void registrarCuidado(Long membroId, String mesRef) {
        if (accRepo.existsByMembroIdAndMesReferencia(membroId, mesRef)) return;

        Membro m = membroRepo.findById(membroId).orElseThrow();
        DiscipuladoAcompanhamento da = new DiscipuladoAcompanhamento();
        da.setMembro(m);
        da.setMesReferencia(mesRef);
        da.setDataAcao(LocalDate.now());
        accRepo.save(da);
    }

    @Cacheable(value = "secretaria-discipulado", key = "'todos'")
    @Transactional(readOnly = true)
    public List<DiscipuladoRelatorioResponseDTO> listarTodosParaSecretaria() {
        return relatorioRepo.findAllWithEagerRelationships() // ← era findAllComDetalhes()
                .stream()
                .map(rel -> new DiscipuladoRelatorioResponseDTO(
                        rel.getId(),
                        rel.getCelula() != null ? rel.getCelula().getNome() : "Sem célula",
                        rel.getLider() != null ? rel.getLider().getNome() : "Líder não informado",
                        rel.getMembro() != null ? rel.getMembro().getNome() : "Membro não informado",
                        rel.getSemanaInicio(),
                        rel.getSemanaFim(),
                        rel.isQuartaNoite(),
                        rel.isQuintaNoite(),
                        rel.isDomingoManha(),
                        rel.isDomingoNoite(),
                        rel.getJustEscolaBiblica(),
                        rel.getJustQuartaNoite(),
                        rel.getJustQuintaNoite(),
                        rel.getJustDomingoManha(),
                        rel.getJustDomingoNoite()
                ))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "alertas-discipulado", key = "#mesRef + '-criticos'")
    @Transactional(readOnly = true)
    public List<AlertaDTO> obterAlertasCriticosPorMes(String mesRef) {
        try {
            String[] partes = mesRef.split("-");
            int ano = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);

            List<Object[]> resultados = relatorioRepo.buscarAlertasDetalhados(mes, ano, mesRef);

            return resultados.stream().map(obj -> new AlertaDTO(
                    ((Number) obj[0]).longValue(),
                    (String)  obj[1],
                    (String)  obj[2],
                    (String)  obj[3],
                    ((Number) obj[4]).intValue()
            )).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Erro ao buscar alertas críticos para {}", mesRef, e);
            return new ArrayList<>();
        }
    }
}