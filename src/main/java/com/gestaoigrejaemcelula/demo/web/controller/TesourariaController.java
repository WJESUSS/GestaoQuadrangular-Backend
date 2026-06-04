package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.LancamentoTesourariaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MembroSelectDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.TesourariaService;
import com.gestaoigrejaemcelula.demo.domain.entity.LancamentoTesouraria;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.LancamentoTesourariaRepository;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/tesouraria")

@PreAuthorize("hasAnyRole('ADMIN', 'TESOUREIRO', 'PASTOR')")
public class TesourariaController {

    private final TesourariaService service;
    private final LancamentoTesourariaRepository lancamentoTesourariaRepository;

    public TesourariaController(TesourariaService service,
                                LancamentoTesourariaRepository lancamentoTesourariaRepository) {
        this.service = service;
        this.lancamentoTesourariaRepository = lancamentoTesourariaRepository;
    }

    @PostMapping("/lancar")
    public ResponseEntity<String> lancar(@RequestBody @Valid LancamentoTesourariaDTO dto) {
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

        LocalDate hoje = LocalDate.now();
        int mesAtual = mes != null ? mes : hoje.getMonthValue();
        int anoAtual = ano != null ? ano : hoje.getYear();

        List<LancamentoTesouraria> registros = service.listarPorMesAno(mesAtual, anoAtual);
        Map<String, BigDecimal> resumo = service.resumoMensal(mesAtual, anoAtual);

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

        LocalDate hoje = LocalDate.now();
        int anoAtual = (ano != null) ? ano : hoje.getYear();

        List<Object[]> resultados = lancamentoTesourariaRepository.comparativoAnual(anoAtual);
        Map<Integer, Object[]> porMes = new HashMap<>();
        for (Object[] row : resultados) {
            porMes.put(((Number) row[0]).intValue(), row);
        }

        List<Map<String, Object>> comparativo = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            Object[] row = porMes.get(m);
            Map<String, Object> mesMap = new HashMap<>();
            mesMap.put("mes", m);
            mesMap.put("totalDizimo", row != null ? row[1] : BigDecimal.ZERO);
            mesMap.put("totalOferta", row != null ? row[2] : BigDecimal.ZERO);
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

        TesourariaService.FieisInfieisMes resultado = service.obterFieisInfieis(mes, ano);

        Map<String, List<Membro>> resposta = new HashMap<>();
        resposta.put("fieis", resultado.getFieis());
        resposta.put("infieis", resultado.getInfieis());

        return ResponseEntity.ok(resposta);
    }
}