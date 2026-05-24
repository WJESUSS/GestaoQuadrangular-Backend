package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.repository.VisitanteRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VisitanteService {

    private final VisitanteRepository repository;
    private final CelulaRepository celulaRepository;
    private final AuditoriaHelper auditoria;

    public VisitanteService(VisitanteRepository repository,
                            CelulaRepository celulaRepository,
                            AuditoriaHelper auditoria) {
        this.repository      = repository;
        this.celulaRepository = celulaRepository;
        this.auditoria       = auditoria;
    }

    // ── Helper ─────────────────────────────────────────────────────────────────
    private String str(Object o) { return o != null ? o.toString() : ""; }

    // =========================
    // CADASTRAR
    // =========================
    @Transactional
    public VisitanteResponseDTO cadastrar(VisitanteRequestDTO dto) {
        Visitante visitante = new Visitante();
        preencher(visitante, dto);
        visitante.setAtivo(true);

        Visitante salvo = repository.save(visitante);

        auditoria.registrar("VISITANTE", salvo.getId(), salvo.getNome(), "CREATE",
                Map.of(
                        "telefone", Map.of("para", str(salvo.getTelefone())),
                        "email",    Map.of("para", str(salvo.getEmail())),
                        "origem",   Map.of("para", str(salvo.getOrigem()))
                )
        );

        return toDTO(salvo);
    }

    // =========================
    // LISTAR TODOS
    // =========================
    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // BUSCAR POR NOME
    // =========================
    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // ATUALIZAR
    // =========================
    @Transactional
    public VisitanteResponseDTO atualizar(Long id, VisitanteRequestDTO dto) {
        Visitante visitante = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

        // Monta diff ANTES de alterar
        Map<String, Object> diff = new LinkedHashMap<>();
        if (!Objects.equals(visitante.getNome(), dto.getNome()))
            diff.put("nome",      Map.of("de", str(visitante.getNome()),      "para", str(dto.getNome())));
        if (!Objects.equals(visitante.getTelefone(), dto.getTelefone()))
            diff.put("telefone",  Map.of("de", str(visitante.getTelefone()),  "para", str(dto.getTelefone())));
        if (!Objects.equals(visitante.getEmail(), dto.getEmail()))
            diff.put("email",     Map.of("de", str(visitante.getEmail()),     "para", str(dto.getEmail())));
        if (!Objects.equals(visitante.getOrigem(), dto.getOrigem()))
            diff.put("origem",    Map.of("de", str(visitante.getOrigem()),    "para", str(dto.getOrigem())));
        if (!Objects.equals(visitante.getResponsavelAcompanhamento(), dto.getResponsavelAcompanhamento()))
            diff.put("responsavel", Map.of("de", str(visitante.getResponsavelAcompanhamento()), "para", str(dto.getResponsavelAcompanhamento())));
        if (visitante.isAtivo() != dto.isAtivo())
            diff.put("ativo",     Map.of("de", str(visitante.isAtivo()),      "para", str(dto.isAtivo())));

        preencher(visitante, dto);
        Visitante salvo = repository.save(visitante);

        if (!diff.isEmpty())
            auditoria.registrar("VISITANTE", salvo.getId(), salvo.getNome(), "UPDATE", diff);

        return toDTO(salvo);
    }

    // =========================
    // LISTAR POR CÉLULA
    // =========================
    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarVisitantesPorCelula(Long celulaId) {
        return repository.findByCelulaIdAndAtivoTrue(celulaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarAtivosPorCelula(Long celulaId) {
        return repository.findByCelulaIdAndAtivoTrue(celulaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // AUXILIARES
    // =========================
    private void preencher(Visitante visitante, VisitanteRequestDTO dto) {
        visitante.setNome(dto.getNome());
        visitante.setTelefone(dto.getTelefone());
        visitante.setEmail(dto.getEmail());
        visitante.setDataPrimeiraVisita(dto.getDataPrimeiraVisita());
        visitante.setOrigem(dto.getOrigem());
        visitante.setResponsavelAcompanhamento(dto.getResponsavelAcompanhamento());
        visitante.setAtivo(dto.isAtivo());

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new RuntimeException("Célula não encontrada com ID: " + dto.getCelulaId()));
            visitante.setCelula(celula);
        }
    }

    private VisitanteResponseDTO toDTO(Visitante visitante) {
        VisitanteResponseDTO dto = new VisitanteResponseDTO();
        dto.setId(visitante.getId());
        dto.setNome(visitante.getNome());
        dto.setTelefone(visitante.getTelefone());
        dto.setEmail(visitante.getEmail());
        dto.setDataPrimeiraVisita(visitante.getDataPrimeiraVisita());
        dto.setOrigem(visitante.getOrigem());
        dto.setResponsavelAcompanhamento(visitante.getResponsavelAcompanhamento());
        dto.setAtivo(visitante.isAtivo());
        return dto;
    }

    private Celula buscarPorId(Long celulaId) {
        return celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));
    }
}