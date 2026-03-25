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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class TesourariaService {

    private final LancamentoTesourariaRepository repository;
    private final MembroRepository membroRepository;

    public TesourariaService(LancamentoTesourariaRepository repository, MembroRepository membroRepository) {
        this.repository = repository;
        this.membroRepository = membroRepository;
    }
    public void lancar(LancamentoTesourariaDTO dto) {
        BigDecimal vDizimo = dto.getValorDizimo() != null ? dto.getValorDizimo() : BigDecimal.ZERO;
        BigDecimal vOferta = dto.getValorOferta() != null ? dto.getValorOferta() : BigDecimal.ZERO;

        if (vDizimo.compareTo(BigDecimal.ZERO) <= 0 && vOferta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Informe pelo menos um valor válido para DÍZIMO ou OFERTA");
        }

        LancamentoTesouraria lancamento = new LancamentoTesouraria();
        lancamento.setMembroNome(dto.getMembroNome());
        lancamento.setValorDizimo(vDizimo.compareTo(BigDecimal.ZERO) > 0 ? vDizimo : null);
        lancamento.setValorOferta(vOferta.compareTo(BigDecimal.ZERO) > 0 ? vOferta : null);
        lancamento.setTipoOferta(vOferta.compareTo(BigDecimal.ZERO) > 0 ? dto.getTipoOferta() : null);
        lancamento.setDataLancamento(dto.getDataLancamento() != null ? dto.getDataLancamento() : LocalDate.now());

        repository.save(lancamento);
    }


    public List<LancamentoTesouraria> listar() {
        return repository.findAll();
    }

    public Map<String, Object> getResumo() {
        Map<String, Object> resumo = new HashMap<>();
        BigDecimal totalBronze = BigDecimal.ZERO;
        BigDecimal totalPrata  = BigDecimal.ZERO;
        BigDecimal totalOuro   = BigDecimal.ZERO;

        for (LancamentoTesouraria l : repository.findAll()) {
            BigDecimal valor = (l.getValorDizimo() != null ? l.getValorDizimo() : BigDecimal.ZERO)
                    .add(l.getValorOferta() != null ? l.getValorOferta() : BigDecimal.ZERO);

            if ("BRONZE".equalsIgnoreCase(l.getTipoOferta())) totalBronze = totalBronze.add(valor);
            else if ("PRATA".equalsIgnoreCase(l.getTipoOferta())) totalPrata = totalPrata.add(valor);
            else if ("OURO".equalsIgnoreCase(l.getTipoOferta())) totalOuro = totalOuro.add(valor);
        }

        resumo.put("BRONZE", totalBronze);
        resumo.put("PRATA", totalPrata);
        resumo.put("OURO", totalOuro);
        return resumo;
    }

    public List<Map<String, Object>> getResumoPorMembro() {
        Map<String, Map<String, Object>> mapMembros = new HashMap<>(); // chave agora é String

        for (LancamentoTesouraria l : repository.findAll()) {
            String membroNome = l.getMembroNome(); // pega o nome
            if (membroNome == null) continue; // ignora lançamentos sem nome

            Map<String, Object> membroMap = mapMembros.getOrDefault(membroNome, new HashMap<>());
            membroMap.put("membroNome", membroNome); // armazena o nome

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


    public List<MembroSelectDTO> listarParaSelect() {
        // Chama o repository que já retorna apenas id e nome de membros ativos
        return membroRepository.listarParaSelect();
    }
    public List<MembroSelectDTO> listarNomesParaSelect() {
        return membroRepository.listarNomesParaSelect();
    }




    public List<LancamentoTesouraria> listarPorMesAno(int mes, int ano) {
        return repository.findByMesAndAno(mes, ano);
    }

    // Resumo mensal de dízimo e ofertas
    public Map<String, BigDecimal> resumoMensal(int mes, int ano) {
        BigDecimal totalDizimo = repository.totalDizimoPorMesAno(mes, ano);
        BigDecimal totalBronze = repository.totalOfertaPorMesAnoETipo(mes, ano, "BRONZE");
        BigDecimal totalPrata = repository.totalOfertaPorMesAnoETipo(mes, ano, "PRATA");
        BigDecimal totalOuro = repository.totalOfertaPorMesAnoETipo(mes, ano, "OURO");

        Map<String, BigDecimal> resumo = new HashMap<>();
        resumo.put("DIZIMO", totalDizimo != null ? totalDizimo : BigDecimal.ZERO);
        resumo.put("BRONZE", totalBronze != null ? totalBronze : BigDecimal.ZERO);
        resumo.put("PRATA", totalPrata != null ? totalPrata : BigDecimal.ZERO);
        resumo.put("OURO", totalOuro != null ? totalOuro : BigDecimal.ZERO);

        return resumo;
    }

    // Total de dízimo por mês/ano
    public BigDecimal totalDizimoPorMesAno(int mes, int ano) {
        BigDecimal total = repository.totalDizimoPorMesAno(mes, ano);
        return total != null ? total : BigDecimal.ZERO;
    }

    // Total de ofertas por mês/ano
    public BigDecimal totalOfertaPorMesAno(int mes, int ano) {
        BigDecimal total = repository.totalOfertaPorMesAno(mes, ano);
        return total != null ? total : BigDecimal.ZERO;
    }

    public FieisInfieisMes obterFieisInfieis(Integer mes, Integer ano) {
        LocalDate hoje = LocalDate.now();
        int mesAtual = (mes != null) ? mes : hoje.getMonthValue();
        int anoAtual = (ano != null) ? ano : hoje.getYear();

        List<Membro> todosMembros = membroRepository.findAll();
        List<Membro> fieis = new ArrayList<>();
        List<Membro> infieis = new ArrayList<>();

        for (Membro m : todosMembros) {
            long qtdLancamentos = repository
                    .countByMembroAndMesAno(m.getNome(), mesAtual, anoAtual);

            if (qtdLancamentos > 0) {
                fieis.add(m);
            } else {
                infieis.add(m);
            }
        }

        return new FieisInfieisMes(fieis, infieis);
    }

    // Classe interna para encapsular fiéis e infiéis
    public static class FieisInfieisMes {
        private List<Membro> fieis;
        private List<Membro> infieis;

        public FieisInfieisMes(List<Membro> fieis, List<Membro> infieis) {
            this.fieis = fieis;
            this.infieis = infieis;
        }

        public List<Membro> getFieis() { return fieis; }
        public List<Membro> getInfieis() { return infieis; }
    }
}


