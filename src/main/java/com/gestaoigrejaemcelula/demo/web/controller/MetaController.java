package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.MetaRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MetaResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.MetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metas")
@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA', 'PASTOR')")
public class MetaController {

    private final MetaService metaService;

    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    /**
     * Criar nova meta
     */
    @PostMapping
    public ResponseEntity<MetaResponseDTO> criar(@RequestBody MetaRequestDTO dto) {
        return ResponseEntity.ok(metaService.criarMeta(dto));
    }

    /**
     * Buscar meta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(metaService.buscarPorId(id));
    }

    /**
     * Listar todas as metas de uma célula
     */
    @GetMapping("/celula/{celulaId}")
    public ResponseEntity<List<MetaResponseDTO>> listarPorCelula(@PathVariable Long celulaId) {
        return ResponseEntity.ok(metaService.listarPorCelula(celulaId));
    }

    /**
     * Listar apenas metas ativas de uma célula
     */
    @GetMapping("/celula/{celulaId}/ativas")
    public ResponseEntity<List<MetaResponseDTO>> listarAtivasPorCelula(@PathVariable Long celulaId) {
        return ResponseEntity.ok(metaService.listarAtivasPorCelula(celulaId));
    }

    /**
     * Incrementar progresso manualmente
     * Usado quando o usuário quer marcar manualmente um progresso
     */
    @PutMapping("/{id}/incrementar")
    public ResponseEntity<MetaResponseDTO> incrementarProgresso(@PathVariable Long id) {
        return ResponseEntity.ok(metaService.incrementarProgresso(id));
    }

    /**
     * Decrementar progresso manualmente
     * Usado quando o usuário quer desmarcar um progresso
     */
    @PutMapping("/{id}/decrementar")
    public ResponseEntity<MetaResponseDTO> decrementarProgresso(@PathVariable Long id) {
        return ResponseEntity.ok(metaService.decrementarProgresso(id));
    }

    /**
     * Atualizar progresso automaticamente baseado em visitantes
     * Este endpoint recalcula o progresso sincronizando com os visitantes da célula
     * Deve ser chamado quando um visitante muda seu status de decisão espiritual
     */
    @PutMapping("/{id}/sincronizar")
    public ResponseEntity<MetaResponseDTO> sincronizarComVisitantes(@PathVariable Long id) {
        return ResponseEntity.ok(metaService.atualizarProgressoAutomatico(id));
    }

    /**
     * Recalcular todas as metas de uma célula
     * Útil quando múltiplos visitantes são atualizados
     */
    @PutMapping("/celula/{celulaId}/recalcular")
    public ResponseEntity<Void> recalcularMetasCelula(@PathVariable Long celulaId) {
        metaService.recalcularTodasMetasCelula(celulaId);
        return ResponseEntity.ok().build();
    }

    /**
     * Atualizar meta
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody MetaRequestDTO dto) {
        return ResponseEntity.ok(metaService.atualizar(id, dto));
    }

    /**
     * Deletar meta
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        metaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Buscar metas próximas de conclusão (80% ou mais)
     */
    @GetMapping("/celula/{celulaId}/proximasconclusao")
    public ResponseEntity<List<MetaResponseDTO>> buscarMetasProximasConclusao(@PathVariable Long celulaId) {
        return ResponseEntity.ok(metaService.buscarMetasProximasConclusao(celulaId));
    }

    /**
     * Buscar metas em atraso (progresso < 50%)
     */
    @GetMapping("/celula/{celulaId}/ematraso")
    public ResponseEntity<List<MetaResponseDTO>> buscarMetasEmAtraso(@PathVariable Long celulaId) {
        return ResponseEntity.ok(metaService.buscarMetasEmAtraso(celulaId));
    }

    /**
     * Contar metas concluídas de uma célula
     */
    @GetMapping("/celula/{celulaId}/contador-concluidas")
    public ResponseEntity<Long> contarMetasConcluidas(@PathVariable Long celulaId) {
        return ResponseEntity.ok(metaService.contarMetasConcluidas(celulaId));
    }
}