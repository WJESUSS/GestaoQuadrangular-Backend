package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.aplication.service.CelulaService;
import com.gestaoigrejaemcelula.demo.aplication.service.DiscipuladoRelatorioService;
import com.gestaoigrejaemcelula.demo.aplication.service.MembroService;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/celulas")
public class CelulaController {

    @Autowired
    private CelulaService service;

    @Autowired
    private MembroService membroService;
@Autowired
    DiscipuladoRelatorioService discipuladoRelatorioService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- ROTAS DO LÍDER DE CÉLULA ---

    @PreAuthorize("hasAuthority('LIDER_CELULA')")
    @GetMapping("/minha-celula")
    public ResponseEntity<CelulaResponseDTO> minhaCelula(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Busca célula **com membros carregados**
        Celula celula = service.buscarCelulaDoLiderComMembros(usuario.getId());

        return ResponseEntity.ok(new CelulaResponseDTO(celula));
    }




    // --- ROTAS DE GESTÃO (ADMIN / SECRETÁRIO) ---
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER','PASTOR')") // Adicionei LIDER se ele puder criar a própria
    @PostMapping
    public ResponseEntity<CelulaResponseDTO> cadastrar(
            @Valid @RequestBody CelulaRequestDTO dto) {

        return ResponseEntity.ok(service.cadastrar(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO','PASTOR')")
    @GetMapping
    public ResponseEntity<List<CelulaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO','PASTOR')")
    @GetMapping("/buscar")
    public ResponseEntity<List<CelulaResponseDTO>> buscar(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO','PASTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<CelulaResponseDTO> atualizar(@PathVariable Long id, @RequestBody CelulaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // --- GESTÃO DE MEMBROS E VISITANTES ---

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA','PASTOR')")
    @PostMapping("/adicionar/{celulaId}/membros/{membroId}")
    public ResponseEntity<Void> adicionarMembro(@PathVariable Long celulaId, @PathVariable Long membroId) {
        service.adicionarMembro(celulaId, membroId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/solicitar-multiplicacao")
    @PreAuthorize("hasRole('LIDER_CELULA')")
    public ResponseEntity<Void> solicitarMultiplicacao(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Principal principal) {
        String motivo = body.get("motivo");
        // Você pode pegar o ID do usuário logado via principal ou service
        Long usuarioId = ((Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        service.solicitarMultiplicacao(id, motivo, usuarioId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA','PASTOR')")
    @DeleteMapping("/{celulaId}/membros/{membroId}")
    public ResponseEntity<Void> removerMembro(@PathVariable Long celulaId, @PathVariable Long membroId) {
        service.removerMembro(celulaId, membroId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO','PASTOR')")
    @PostMapping("/transferir-membro")
    public ResponseEntity<Void> transferirMembro(@RequestBody TransferirMembroDTO dto) {
        service.transferirMembro(dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA','PASTOR')")
    @GetMapping("/{celulaId}/membros")
    public ResponseEntity<List<MembroCelulaDTO>> listarMembrosDaCelula(@PathVariable Long celulaId) {
        return ResponseEntity.ok(membroService.listarMembrosPorCelula(celulaId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA','PASTOR')")
    @PostMapping("/{id}/visitantes")
    public ResponseEntity<VisitanteResponseDTO> adicionarVisitanteACelula(@PathVariable Long id, @RequestBody VisitanteRequestDTO dto) {
        return ResponseEntity.ok(service.salvarVisitanteNaCelula(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'LIDER_CELULA','PASTOR')")
    @GetMapping("/{id}/visitantes")
    public ResponseEntity<List<VisitanteResponseDTO>> listarVisitantesPorCelula(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarVisitantesPorCelula(id));
    }
    @GetMapping("/solicitacoes-multiplicacao")
    @PreAuthorize("hasAnyRole('SECRETARIO', 'PASTOR')")
    public ResponseEntity<List<CelulaResumoDTO>> listarSolicitacoes() {
        // Busca no banco células que não estão com status NORMAL
        List<CelulaResumoDTO> solicitacoes = service.buscarSolicitacoesPendentes();
        return ResponseEntity.ok(solicitacoes);
    }
    @PostMapping("/{id}/decidir-multiplicacao")
    @PreAuthorize("hasAnyRole('SECRETARIO', 'PASTOR')")
    public ResponseEntity<Void> decidirMultiplicacao(
            @PathVariable Long id,
            @RequestBody DecisaoDTO decisao) {

        // O service vai mudar o status para APROVADO ou REJEITADO
        service.decidirMultiplicacao(id, decisao.aprovado());
        return ResponseEntity.ok().build();
    }

}