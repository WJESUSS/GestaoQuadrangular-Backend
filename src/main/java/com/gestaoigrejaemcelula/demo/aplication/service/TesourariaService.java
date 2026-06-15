package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.LancamentoTesourariaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MembroSelectDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.LancamentoTesouraria;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.LancamentoTesourariaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TesourariaService {

    private final LancamentoTesourariaRepository repository;
    private final MembroRepository membroRepository;
    private final AuditoriaHelper auditoria;

    public TesourariaService(LancamentoTesourariaRepository repository,
                             MembroRepository membroRepository,
                             AuditoriaHelper auditoria) {
        this.repository       = repository;
        this.membroRepository = membroRepository;
        this.auditoria        = auditoria;
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

        Map<String, Object> detalhes = new HashMap<>();
        detalhes.put("membro", Map.of("para", str(salvo.getMembroNome())));
        detalhes.put("data",   Map.of("para", str(salvo.getDataLancamento())));
        if (vDizimo.compareTo(BigDecimal.ZERO) > 0)
            detalhes.put("dizimo", Map.of("para", str(vDizimo)));
        if (vOferta.compareTo(BigDecimal.ZERO) > 0) {
            detalhes.put("oferta",     Map.of("para", str(vOferta)));
            detalhes.put("tipoOferta", Map.of("para", str(salvo.getTipoOferta())));
        }

        auditoria.registrar("SECRETARIA", salvo.getId(), salvo.getMembroNome(), "CREATE", detalhes);
    }

    // =========================
    // LISTAR TODOS (paginado)
    // =========================
    @Transactional(readOnly = true)
    public Page<LancamentoTesouraria> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // =========================
    // RESUMO GERAL POR TIPO
    // =========================
    @Transactional(readOnly = true)
    public Map<String, Object> getResumo() {
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("BRONZE", BigDecimal.ZERO);
        resumo.put("PRATA",  BigDecimal.ZERO);
        resumo.put("OURO",   BigDecimal.ZERO);

        List<Object[]> resultados = repository.sumAgrupadoPorTipo();
        for (Object[] row : resultados) {
            String tipo = (String) row[0];
            BigDecimal valor = (BigDecimal) row[1];
            if (tipo != null && resumo.containsKey(tipo.toUpperCase())) {
                resumo.put(tipo.toUpperCase(), valor);
            }
        }
        return resumo;
    }

    // =========================
    // RESUMO POR MEMBRO (paginado)
    // =========================
    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getResumoPorMembro(Pageable pageable) {
        // Busca a página de resultados nativos com offset/limit
        Page<Object[]> paginaRaw = repository.sumAgrupadoPorMembroPaginado(pageable);

        List<Map<String, Object>> lista = paginaRaw.getContent().stream()
                .map(row -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("membroNome",  row[0]);
                    m.put("totalDizimo", row[1] != null ? row[1] : BigDecimal.ZERO);
                    m.put("totalOferta", row[2] != null ? row[2] : BigDecimal.ZERO);
                    return m;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(lista, pageable, paginaRaw.getTotalElements());
    }

    // =========================
    // SELECTS (sem paginação — uso em dropdowns)
    // =========================
    @Transactional(readOnly = true)
    public List<MembroSelectDTO> listarParaSelect() {
        return membroRepository.listarParaSelect();
    }

    @Transactional(readOnly = true)
    public List<MembroSelectDTO> listarNomesParaSelect() {
        return membroRepository.listarNomesParaSelect();
    }

    // =========================
    // LISTAR POR MÊS/ANO (paginado)
    // =========================
    @Transactional(readOnly = true)
    public Page<LancamentoTesouraria> listarPorMesAno(int mes, int ano, Pageable pageable) {
        return repository.findByMesAndAno(mes, ano, pageable);
    }

    // =========================
    // RESUMO MENSAL
    // =========================
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public BigDecimal totalDizimoPorMesAno(int mes, int ano) {
        BigDecimal total = repository.totalDizimoPorMesAno(mes, ano);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal totalOfertaPorMesAno(int mes, int ano) {
        BigDecimal total = repository.totalOfertaPorMesAno(mes, ano);
        return total != null ? total : BigDecimal.ZERO;
    }

    // =========================
    // FIÉIS / INFIÉIS (paginado)
    // =========================
    @Transactional(readOnly = true)
    public FieisInfieisMes obterFieisInfieis(Integer mes, Integer ano) {
        LocalDate hoje  = LocalDate.now();
        int mesAtual    = (mes != null) ? mes : hoje.getMonthValue();
        int anoAtual    = (ano != null) ? ano : hoje.getYear();

        Set<String> membrosComLancamento = new HashSet<>(
                repository.findMembrosComLancamentoNoMes(mesAtual, anoAtual)
        );

        List<Membro> todosMembros = membroRepository.findAll();
        List<Membro> fieis        = new ArrayList<>();
        List<Membro> infieis      = new ArrayList<>();

        for (Membro m : todosMembros) {
            if (membrosComLancamento.contains(m.getNome())) fieis.add(m);
            else infieis.add(m);
        }

        return new FieisInfieisMes(fieis, infieis);
    }

    @Transactional(readOnly = true)
    public Page<Membro> obterFieisPaginado(Integer mes, Integer ano, Pageable pageable) {
        LocalDate hoje = LocalDate.now();
        int mesAtual   = (mes != null) ? mes : hoje.getMonthValue();
        int anoAtual   = (ano != null) ? ano : hoje.getYear();
        return repository.findMembrosFieis(mesAtual, anoAtual, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Membro> obterInfieisPaginado(Integer mes, Integer ano, Pageable pageable) {
        LocalDate hoje = LocalDate.now();
        int mesAtual   = (mes != null) ? mes : hoje.getMonthValue();
        int anoAtual   = (ano != null) ? ano : hoje.getYear();
        return repository.findMembrosInfieis(mesAtual, anoAtual, pageable);
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