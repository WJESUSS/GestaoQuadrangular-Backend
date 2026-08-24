package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoColetivoRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoColetivoResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoHistoricoItemDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoIndicadoresDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoIndividualRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoIndividualResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoMembroHistoricoResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoMembroItemDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.CelulaResumoDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.AcompanhamentoDiscipuladoService;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEstudoDiscipulado;
import com.gestaoigrejaemcelula.demo.web.handler.DiscipuladoDuplicadoSemanaException;
import com.gestaoigrejaemcelula.demo.web.handler.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Acompanhamento de Discipulado",
        description = "Registro e acompanhamento dos discipulados individuais e coletivos realizados pelos líderes com os membros de suas células")
@RestController
@RequestMapping("/api/acompanhamento/discipulado")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('LIDER_CELULA','ADMIN','PASTOR')")
public class AcompanhamentoDiscipuladoController {

    private final AcompanhamentoDiscipuladoService service;

    @Operation(summary = "Lista os membros da célula com status semanal e totais de discipulado")
    @GetMapping("/membros")
    public ResponseEntity<List<AcompanhamentoMembroItemDTO>> listarMembros(
            Authentication authentication,
            @Parameter(description = "Obrigatório apenas para ADMIN/PASTOR")
            @RequestParam(required = false) Long celulaId) {
        return ResponseEntity.ok(service.listarMembrosDaCelula(authentication, celulaId));
    }

    @Operation(summary = "Registra um Discipulado Individual",
            description = "Cada membro pode receber apenas 1 discipulado individual por semana. Gera +5 pontos para a célula.")
    @PostMapping("/individual")
    public ResponseEntity<AcompanhamentoIndividualResponseDTO> registrarIndividual(
            Authentication authentication,
            @Valid @RequestBody AcompanhamentoIndividualRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registrarIndividual(authentication, dto));
    }

    @Operation(summary = "Histórico de discipulados individuais de um membro")
    @GetMapping("/individual/historico/{membroId}")
    public ResponseEntity<AcompanhamentoMembroHistoricoResponseDTO> historicoDoMembro(
            Authentication authentication,
            @PathVariable Long membroId,
            @Parameter(description = "Obrigatório apenas para ADMIN/PASTOR")
            @RequestParam(required = false) Long celulaId) {
        return ResponseEntity.ok(service.historicoDoMembro(authentication, membroId, celulaId));
    }

    @Operation(summary = "Cancela um Discipulado Individual", description = "Registros cancelados não geram pontuação.")
    @PatchMapping("/individual/{id}/cancelar")
    public ResponseEntity<Void> cancelarIndividual(Authentication authentication, @PathVariable Long id) {
        service.cancelarIndividual(authentication, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Registra um Discipulado Coletivo",
            description = "Pontuação calculada por participante presente: participantes × 5 pontos.")
    @PostMapping("/coletivo")
    public ResponseEntity<AcompanhamentoColetivoResponseDTO> registrarColetivo(
            Authentication authentication,
            @Valid @RequestBody AcompanhamentoColetivoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registrarColetivo(authentication, dto));
    }

    @Operation(summary = "Detalhes de um Discipulado Coletivo", description = "Retorna a lista completa de presentes e a pontuação gerada.")
    @GetMapping("/coletivo/{id}")
    public ResponseEntity<AcompanhamentoColetivoResponseDTO> detalharColetivo(
            Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(service.detalharColetivo(authentication, id));
    }

    @Operation(summary = "Cancela um Discipulado Coletivo", description = "Registros cancelados não geram pontuação.")
    @PatchMapping("/coletivo/{id}/cancelar")
    public ResponseEntity<Void> cancelarColetivo(Authentication authentication, @PathVariable Long id) {
        service.cancelarColetivo(authentication, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Histórico geral com filtros")
    @GetMapping("/historico")
    public ResponseEntity<List<AcompanhamentoHistoricoItemDTO>> historicoGeral(
            Authentication authentication,
            @Parameter(description = "Obrigatório apenas para ADMIN/PASTOR")
            @RequestParam(required = false) Long celulaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long membroId,
            @Parameter(description = "INDIVIDUAL ou COLETIVO. Vazio retorna ambos.")
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) TipoEstudoDiscipulado tipoEstudo) {
        return ResponseEntity.ok(service.historicoGeral(
                authentication, celulaId, dataInicio, dataFim, membroId, tipo, tema, tipoEstudo));
    }

    @Operation(summary = "Indicadores do dashboard do líder")
    @GetMapping("/indicadores")
    public ResponseEntity<AcompanhamentoIndicadoresDTO> indicadores(
            Authentication authentication,
            @Parameter(description = "Obrigatório apenas para ADMIN/PASTOR")
            @RequestParam(required = false) Long celulaId) {
        return ResponseEntity.ok(service.indicadores(authentication, celulaId));
    }

    @Operation(summary = "Células vinculadas ao pastor logado", description = "Para popular o filtro de células na tela do pastor. ADMIN recebe todas as células ativas.")
    @PreAuthorize("hasAnyAuthority('PASTOR','ADMIN')")
    @GetMapping("/pastor/celulas")
    public ResponseEntity<List<CelulaResumoDTO>> listarCelulasDoPastor(Authentication authentication) {
        return ResponseEntity.ok(service.listarCelulasDoPastor(authentication));
    }

    @Operation(summary = "Histórico consolidado de todos os discipulados das células do pastor",
            description = "Retorna os relatórios individuais e coletivos de todas as células pastoreadas. "
                    + "Filtros opcionais: período, membro, tipo (INDIVIDUAL/COLETIVO), tema e tipo de estudo. "
                    + "ADMIN pode filtrar por celulaId; sem filtro, retorna todas as células.")
    @PreAuthorize("hasAnyAuthority('PASTOR','ADMIN')")
    @GetMapping("/pastor/historico")
    public ResponseEntity<List<AcompanhamentoHistoricoItemDTO>> historicoPastor(
            Authentication authentication,
            @Parameter(description = "Opcional: restringe a uma célula específica")
            @RequestParam(required = false) Long celulaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long membroId,
            @Parameter(description = "INDIVIDUAL ou COLETIVO. Vazio retorna ambos.")
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) TipoEstudoDiscipulado tipoEstudo) {
        return ResponseEntity.ok(service.historicoPastor(
                authentication, celulaId, dataInicio, dataFim, membroId, tipo, tema, tipoEstudo));
    }

    @ExceptionHandler(DiscipuladoDuplicadoSemanaException.class)
    public ResponseEntity<GlobalExceptionHandler.ErrorResponse> handleDuplicidade(
            DiscipuladoDuplicadoSemanaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new GlobalExceptionHandler.ErrorResponse(
                        409,
                        "Discipulado duplicado",
                        ex.getMessage(),
                        "MEMBRO_JA_DISCIPULADO_SEMANA"
                ));
    }
}
