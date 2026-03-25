package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.RankingCelulaDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.RankingCelulaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelos endpoints de ranking de células.
 */
@Tag(name = "Ranking de Células", description = "Endpoints para consulta de rankings mensais das células")
@Validated
@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "*", maxAge = 3600) // Ajuste os origins em produção!
@RequiredArgsConstructor // Substitui @Autowired field injection
public class RankingCelulaController {

    private final RankingCelulaService service;

    /**
     * Retorna o ranking de células para um determinado mês.
     *
     * @param mes formato YYYY-MM (ex: 2026-02)
     * @return Lista ordenada de RankingCelulaDTO
     */
    @Operation(
            summary = "Consultar ranking mensal de células",
            description = "Retorna as células ordenadas por pontuação para o mês informado"
    )
    @GetMapping("/celulas")
    public ResponseEntity<List<RankingCelulaDTO>> getRankingCelulas(
            @Parameter(description = "Mês no formato YYYY-MM", example = "2026-02", required = true)
            @RequestParam
            @Pattern(regexp = "\\d{4}-\\d{2}", message = "Formato de mês inválido. Use YYYY-MM")
            String mes) {

        List<RankingCelulaDTO> ranking = service.gerarRanking(mes);

        if (ranking.isEmpty()) {
            return ResponseEntity.ok(List.of()); // 200 OK com lista vazia é melhor que 204 em muitos casos
        }

        return ResponseEntity.ok(ranking);
    }

    // Opcional: endpoint para o ranking do mês atual (sem precisar passar parâmetro)
    @Operation(summary = "Ranking do mês atual")
    @GetMapping("/celulas/atual")
    public ResponseEntity<List<RankingCelulaDTO>> getRankingAtual() {
        String mesAtual = java.time.YearMonth.now().toString(); // "2026-02"
        return getRankingCelulas(mesAtual);
    }
}