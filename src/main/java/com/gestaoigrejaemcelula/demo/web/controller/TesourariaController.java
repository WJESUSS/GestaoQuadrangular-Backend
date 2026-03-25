package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.LancamentoTesourariaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MembroSelectDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.TesourariaService;
import com.gestaoigrejaemcelula.demo.domain.entity.LancamentoTesouraria;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.LancamentoTesourariaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.PrivilegedAction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tesouraria")
@CrossOrigin(origins = "http://localhost:5173")
public class TesourariaController {

    private final TesourariaService service;
    private final MembroRepository membroRepository;
    private final LancamentoTesourariaRepository lancamentoTesourariaRepository;

    public TesourariaController(TesourariaService service, MembroRepository membroRepository, LancamentoTesourariaRepository lancamentoTesourariaRepository) {
        this.service = service;
        this.membroRepository = membroRepository;
        this.lancamentoTesourariaRepository = lancamentoTesourariaRepository;
    }

    @PostMapping("/lancar")
    public ResponseEntity<String> lancar(@RequestBody LancamentoTesourariaDTO dto) {
        service.lancar(dto);
        return ResponseEntity.ok("Lançamento registrado com sucesso!");
    }

    @GetMapping("/listar")
    public ResponseEntity<List<LancamentoTesouraria>> listar() {
        return ResponseEntity.ok(service.listar());
    }
    @GetMapping("/resumo")
    public Map<String, Object> resumo() {
        return service.getResumo();
    }
    @GetMapping("/membros-resumo")
    public List<Map<String, Object>> membrosResumo() {
        return service.getResumoPorMembro();
    }

    @GetMapping("/select")
    public ResponseEntity<List<MembroSelectDTO>> listarParaSelect() {
        return ResponseEntity.ok(service.listarParaSelect());
    }
    @GetMapping("/select-nome")
    public ResponseEntity<List<MembroSelectDTO>> listarNomesParaSelect() {
        return ResponseEntity.ok(service.listarNomesParaSelect());
    }
    @GetMapping("/relatorio-tesouraria")
    public ResponseEntity<Map<String, Object>> relatorioMensal(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {

        // Se não passar, pega mês/ano atual
        java.time.LocalDate hoje = java.time.LocalDate.now();
        int mesAtual = mes != null ? mes : hoje.getMonthValue();
        int anoAtual = ano != null ? ano : hoje.getYear();

        List<LancamentoTesouraria> registros = service.listarPorMesAno(mesAtual, anoAtual);
        Map<String, java.math.BigDecimal> resumo = service.resumoMensal(mesAtual, anoAtual);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("registros", registros);
        resposta.put("resumo", resumo);
        resposta.put("mes", mesAtual);
        resposta.put("ano", anoAtual);

        return ResponseEntity.ok(resposta);
    }
    @GetMapping("/comparativo-anual")
    public ResponseEntity<Map<String, Object>> comparativoAnual(
            @RequestParam(required = false) Integer ano) {

        java.time.LocalDate hoje = java.time.LocalDate.now();
        int anoAtual = (ano != null) ? ano : hoje.getYear();

        // Lista com 12 meses, cada mês com total de dizimo e oferta
        List<Map<String, Object>> comparativo = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            BigDecimal totalDizimo = service.totalDizimoPorMesAno(m, anoAtual);
            BigDecimal totalOferta = service.totalOfertaPorMesAno(m, anoAtual);

            Map<String, Object> mesMap = new HashMap<>();
            mesMap.put("mes", m);
            mesMap.put("totalDizimo", totalDizimo != null ? totalDizimo : BigDecimal.ZERO);
            mesMap.put("totalOferta", totalOferta != null ? totalOferta : BigDecimal.ZERO);

            comparativo.add(mesMap);
        }

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("ano", anoAtual);
        resposta.put("comparativo", comparativo);

        return ResponseEntity.ok(resposta);
    }
    @GetMapping("/fieis-infieis-mes")
    public ResponseEntity<Map<String, List<Membro>>> fieisInfieisMes(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {

        LocalDate hoje = LocalDate.now();
        int mesAtual = (mes != null) ? mes : hoje.getMonthValue();
        int anoAtual = (ano != null) ? ano : hoje.getYear();

        List<Membro> todosMembros = membroRepository.findAll();
        List<Membro> infieis = new ArrayList<>();
        List<Membro> fieis = new ArrayList<>();

        for (Membro m : todosMembros) {
            // conta lançamentos do membro no mês
            long lancamentos = lancamentoTesourariaRepository
                    .countByMembroAndMesAno(m.getNome(), mesAtual, anoAtual);

            if (lancamentos > 0) {
                fieis.add(m);
            } else {
                infieis.add(m);
            }
        }

        Map<String, List<Membro>> resultado = new HashMap<>();
        resultado.put("fieis", fieis);
        resultado.put("infieis", infieis);

        return ResponseEntity.ok(resultado);
    }

}
