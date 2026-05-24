package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.LancamentoTesourariaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MembroSelectDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.LancamentoTesouraria;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.LancamentoTesourariaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TesourariaService {

    private final LancamentoTesourariaRepository repository;
    private final MembroRepository membroRepository;
    private final AuditoriaHelper auditoria;

    public TesourariaService(LancamentoTesourariaRepository repository,
                             MembroRepository membroRepository,
                             AuditoriaHelper auditoria) {
        this.repository      = repository;
        this.membroRepository = membroRepository;
        this.auditoria       = auditoria;
    }

    // ── Helper ─────────────────────────────────────────────────────────────────
    private String str(Object o) { return o != null ? o.toString() : ""; }

    // =========================
    // LANÇAR
    // =========================
    public void lancar(LancamentoTesourariaDTO dto) {
        BigDecimal vDizimo = dto.getValorDizimo() != null ? dto.getValorDizimo() : BigDecimal.ZERO;
        BigDecimal vOferta = dto.getValorOferta() != null ? dto.getValorOferta() : BigDecimal.ZERO;

        if (vDizimo.compareTo(BigDecimal.ZERO) <= 0 && vOferta.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Informe pelo menos um valor válido para DÍZIMO ou OFERTA");

        LancamentoTesouraria lancamento = new LancamentoTesouraria();
        lancamento.setMembroNome(dto.getMembroNome());
        lancamento.setValorDizimo(vDizimo.compareTo(BigDecimal.ZERO) > 0 ? vDizimo : null);
        lancamento.setValorOferta(vOferta.compareTo(BigDecimal.ZERO) > 0 ? vOferta : null);
        lancamento.setTipoOferta(vOferta.compareTo(BigDecimal.ZERO) > 0 ? dto.getTipoOferta() : null);
        lancamento.setDataLancamento(dto.getDataLancamento() != null ? dto.getDataLancamento() : LocalDate.now());

        LancamentoTesouraria salvo = repository.save(lancamento);

        // Monta detalhes do lançamento para auditoria
        Map<String, Object> detalhes = new HashMap<>();
        detalhes.put("membro",   Map.of("para", str(salvo.getMembroNome())));
        detalhes.put("data",     Map.of("para", str(salvo.getDataLancamento())));
        if (vDizimo.compareTo(BigDecimal.ZERO) > 0)
            detalhes.put("dizimo",  Map.of("para", str(vDizimo)));
        if (vOferta.compareTo(BigDecimal.ZERO) > 0) {
            detalhes.put("oferta",    Map.of("para", str(vOferta)));
            detalhes.put("tipoOferta", Map.of("para", str(salvo.getTipoOferta())));
        }

        auditoria.registrar("SECRETARIA", salvo.getId(), salvo.getMembroNome(), "CREATE", detalhes);
    }

    // =========================
    // LISTAR TODOS
    // =========================
    public List<LancamentoTesouraria> listar() {
        return repository.findAll();
    }

    // =========================
    // RESUMO GERAL POR TIPO
    // =========================
    public Map<String, Object> getResumo() {
        Map<String, Object> resumo = new HashMap<>();
        BigDecimal totalBronze = BigDecimal.ZERO;
        BigDecimal totalPrata  = BigDecimal.ZERO;
        BigDecimal totalOuro   = BigDecimal.ZERO;

        for (LancamentoTesouraria l : repository.findAll()) {
            BigDecimal valor = (l.getValorDizimo() != null ? l.getValorDizimo() : BigDecimal.ZERO)
                    .add(l.getValorOferta() != null ? l.getValorOferta() : BigDecimal.ZERO);

            if      ("BRONZE".equalsIgnoreCase(l.getTipoOferta())) totalBronze = totalBronze.add(valor);
            else if ("PRATA".equalsIgnoreCase(l.getTipoOferta()))  totalPrata  = totalPrata.add(valor);
            else if ("OURO".equalsIgnoreCase(l.getTipoOferta()))   totalOuro   = totalOuro.add(valor);
        }

        resumo.put("BRONZE", totalBronze);
        resumo.put("PRATA",  totalPrata);
        resumo.put("OURO",   totalOuro);
        return resumo;
    }

    // =========================
    // RESUMO POR MEMBRO
    // =========================
    public List<Map<String, Object>> getResumoPorMembro() {
        Map<String, Map<String, Object>> mapMembros = new HashMap<>();

        for (LancamentoTesouraria l : repository.findAll()) {
            String membroNome = l.getMembroNome();
            if (membroNome == null) continue;

            Map<String, Object> membroMap = mapMembros.getOrDefault(membroNome, new HashMap<>());
            membroMap.put("membroNome", membroNome);

            BigDecimal totalDizimo = (BigDecimal) membroMap.getOrDefault("totalDizimo", BigDecimal.ZERO);
            BigDecimal totalOferta = (BigDecimal) membroMap.getOrDefault("totalOferta", BigDecimal.ZERO);

            totalDizimo = totalDizimo.add(l.getValorDizimo() != null ? l.getValorDizimo() : BigDecimal.ZERO);
            totalOferta = totalOferta.add(l.getValorOferta() != null ? l.getValorOferta() : BigDecimal.ZERO);

            membroMap.put("totalDizimo", totalDizimo);
            membroMap.put("totalOferta", totalOferta);
            mapMembros.put(membroNome, membroMap);
        }

        return new ArrayList<>(mapMembros.values());
    }

    // =========================
    // SELECTS
    // =========================
    public List<MembroSelectDTO> listarParaSelect() {
        return membroRepository.listarParaSelect();
    }

    public List<MembroSelectDTO> listarNomesParaSelect() {
        return membroRepository.listarNomesParaSelect();
    }

    // =========================
    // LISTAR POR MÊS/ANO
    // =========================
    public List<LancamentoTesouraria> listarPorMesAno(int mes, int ano) {
        return repository.findByMesAndAno(mes, ano);
    }

    // =========================
    // RESUMO MENSAL
    // =========================
    public Map<String, BigDecimal> resumoMensal(int mes, int ano) {
        BigDecimal totalDizimo = repository.totalDizimoPorMesAno(mes, ano);
        BigDecimal totalBronze = repository.totalOfertaPorMesAnoETipo(mes, ano, "BRONZE");
        BigDecimal totalPrata  = repository.totalOfertaPorMesAnoETipo(mes, ano, "PRATA");
        BigDecimal totalOuro   = repository.totalOfertaPorMesAnoETipo(mes, ano, "OURO");

        Map<String, BigDecimal> resumo = new HashMap<>();
        resumo.put("DIZIMO", totalDizimo != null ? totalDizimo : BigDecimal.ZERO);
        resumo.put("BRONZE", totalBronze != null ? totalBronze : BigDecimal.ZERO);
        resumo.put("PRATA",  totalPrata  != null ? totalPrata  : BigDecimal.ZERO);
        resumo.put("OURO",   totalOuro   != null ? totalOuro   : BigDecimal.ZERO);

        return resumo;
    }

    // =========================
    // TOTAIS INDIVIDUAIS
    // =========================
    public BigDecimal totalDizimoPorMesAno(int mes, int ano) {
        BigDecimal total = repository.totalDizimoPorMesAno(mes, ano);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal totalOfertaPorMesAno(int mes, int ano) {
        BigDecimal total = repository.totalOfertaPorMesAno(mes, ano);
        return total != null ? total : BigDecimal.ZERO;
    }

    // =========================
    // FIÉIS / INFIÉIS
    // =========================
    public FieisInfieisMes obterFieisInfieis(Integer mes, Integer ano) {
        LocalDate hoje   = LocalDate.now();
        int mesAtual     = (mes != null) ? mes : hoje.getMonthValue();
        int anoAtual     = (ano != null) ? ano : hoje.getYear();

        List<Membro> todosMembros = membroRepository.findAll();
        List<Membro> fieis        = new ArrayList<>();
        List<Membro> infieis      = new ArrayList<>();

        for (Membro m : todosMembros) {
            long qtdLancamentos = repository.countByMembroAndMesAno(m.getNome(), mesAtual, anoAtual);
            if (qtdLancamentos > 0) fieis.add(m);
            else                    infieis.add(m);
        }

        return new FieisInfieisMes(fieis, infieis);
    }

    // =========================
    // CLASSE INTERNA
    // =========================
    public static class FieisInfieisMes {
        private final List<Membro> fieis;
        private final List<Membro> infieis;

        public FieisInfieisMes(List<Membro> fieis, List<Membro> infieis) {
            this.fieis   = fieis;
            this.infieis = infieis;
        }

        public List<Membro> getFieis()   { return fieis; }
        public List<Membro> getInfieis() { return infieis; }
    }
}