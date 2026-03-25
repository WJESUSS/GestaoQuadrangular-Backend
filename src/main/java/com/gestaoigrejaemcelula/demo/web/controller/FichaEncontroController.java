package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.FichaEncontroRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.FichaEncontroResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.FichaEncontroService;
import com.gestaoigrejaemcelula.demo.aplication.service.RelatorioEncontroService;
import com.gestaoigrejaemcelula.demo.aplication.service.UsuarioService;
import com.gestaoigrejaemcelula.demo.domain.entity.FichaEncontro;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fichas-encontro")
@PreAuthorize("hasRole('ADMIN') or hasRole('SECRETARIO') or hasRole('LIDER_CELULA') or hasRole('PASTOR')")
@CrossOrigin(origins = "http://localhost:5173")
public class FichaEncontroController {

    private final FichaEncontroService service;
    private final UsuarioService usuarioService;
    private final RelatorioEncontroService relatorioEncontroService;

    public FichaEncontroController(FichaEncontroService service, UsuarioService usuarioService, RelatorioEncontroService relatorioEncontroService) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.relatorioEncontroService = relatorioEncontroService;
    }

    // --- MÉTODOS DE CRIAÇÃO E BUSCA BÁSICA ---
    @PostMapping
    public ResponseEntity<FichaEncontroResponseDTO> criar(@RequestBody FichaEncontroRequestDTO dto) {
        // O service já devolve o ResponseDTO pronto e preenchido
        FichaEncontroResponseDTO resposta = service.criar(dto);

        // Retornamos 201 Created (boa prática para POST) ou 200 OK
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
    @GetMapping("/minhas-fichas")
    public List<FichaEncontroResponseDTO> minhasFichas(Principal principal) {
        String username = principal.getName();
        return usuarioService.findByUsuarioLogado(username);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaEncontroRequestDTO> buscar(@PathVariable Long id) {
        FichaEncontro ficha = service.buscarPorId(id);
        return ResponseEntity.ok(service.converterEntidadeParaDto(ficha));
    }


    @GetMapping
    public ResponseEntity<List<FichaEncontro>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // --- NOVOS MÉTODOS: ATUALIZAÇÃO E EXCLUSÃO ---

    @PutMapping("/{id}")
    public ResponseEntity<FichaEncontro> atualizar(@PathVariable Long id, @RequestBody FichaEncontro ficha) {
        // Geralmente o service trata a lógica de verificar se o ID existe
        return ResponseEntity.ok(service.atualizar(id, ficha));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASTOR') or hasRole('SECRETARIO')" ) // Restringe exclusão a cargos mais altos
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/buscar-por-nome")
    public ResponseEntity<List<FichaEncontro>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(service.buscarPorNomeConvidado(nome));
    }

    @GetMapping("/por-encontro/{nomeEncontro}")
    public ResponseEntity<List<FichaEncontro>> listarPorEncontro(@PathVariable String nomeEncontro) {
        return ResponseEntity.ok(service.listarPorEncontro(nomeEncontro));
    }

    @GetMapping("/contagem")
    public ResponseEntity<Long> contarTotal() {
        return ResponseEntity.ok(service.contarTotalFichas());
    }
    @GetMapping("/listar-Data")
    public ResponseEntity<List<FichaEncontroResponseDTO>> listarPorData(
            @RequestParam(required = false) LocalDate data
    ) {

        return ResponseEntity.ok(service.buscarPorData(data));
    }
}