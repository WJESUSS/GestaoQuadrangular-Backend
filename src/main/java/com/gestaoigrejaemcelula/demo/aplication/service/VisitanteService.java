package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.HistoricoDecisaoDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.repository.VisitanteRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final MetaService metaService;

    public VisitanteService(VisitanteRepository repository,
                            CelulaRepository celulaRepository,
                            AuditoriaHelper auditoria,
                            MetaService metaService) {
        this.repository = repository;
        this.celulaRepository = celulaRepository;
        this.auditoria = auditoria;
        this.metaService = metaService;
    }

    // Helper
    private String str(Object o) {
        return o != null ? o.toString() : "";
    }

    // =========================
    // CADASTRAR
    // =========================
    @Transactional
    public VisitanteResponseDTO cadastrar(VisitanteRequestDTO dto) {
        Visitante visitante = new Visitante();
        preencher(visitante, dto);
        visitante.setAtivo(true);
        visitante.setArquivado(false);

        Visitante salvo = repository.save(visitante);

        auditoria.registrar("VISITANTE", salvo.getId(), salvo.getNome(), "CREATE",
                Map.of(
                        "telefone", Map.of("para", str(salvo.getTelefone())),
                        "email", Map.of("para", str(salvo.getEmail())),
                        "origem", Map.of("para", str(salvo.getOrigem()))
                )
        );

        if (salvo.getCelula() != null) {
            metaService.recalcularTodasMetasCelula(salvo.getCelula().getId());
        }

        return toDTO(salvo);
    }

    // =========================
    // LISTAR TODOS (exclui arquivados)
    // =========================
    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listar() {
        return repository.findAllByArquivadoFalse()
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

        Map<String, Object> diff = new LinkedHashMap<>();
        if (!Objects.equals(visitante.getNome(), dto.getNome()))
            diff.put("nome", Map.of("de", str(visitante.getNome()), "para", str(dto.getNome())));
        if (!Objects.equals(visitante.getTelefone(), dto.getTelefone()))
            diff.put("telefone", Map.of("de", str(visitante.getTelefone()), "para", str(dto.getTelefone())));
        if (!Objects.equals(visitante.getEmail(), dto.getEmail()))
            diff.put("email", Map.of("de", str(visitante.getEmail()), "para", str(dto.getEmail())));
        if (!Objects.equals(visitante.getOrigem(), dto.getOrigem()))
            diff.put("origem", Map.of("de", str(visitante.getOrigem()), "para", str(dto.getOrigem())));
        if (!Objects.equals(visitante.getResponsavelAcompanhamento(), dto.getResponsavelAcompanhamento()))
            diff.put("responsavel", Map.of("de", str(visitante.getResponsavelAcompanhamento()), "para", str(dto.getResponsavelAcompanhamento())));
        if (visitante.isAtivo() != dto.isAtivo())
            diff.put("ativo", Map.of("de", str(visitante.isAtivo()), "para", str(dto.isAtivo())));
        if (!Objects.equals(visitante.getDecisaoEspiritual(), dto.getDecisaoEspiritual()))
            diff.put("decisaoEspiritual", Map.of("de", str(visitante.getDecisaoEspiritual()), "para", str(dto.getDecisaoEspiritual())));

        preencher(visitante, dto);
        visitante.setDecisaoEspiritual(dto.getDecisaoEspiritual());

        Visitante salvo = repository.save(visitante);

        if (!diff.isEmpty())
            auditoria.registrar("VISITANTE", salvo.getId(), salvo.getNome(), "UPDATE", diff);

        if (salvo.getCelula() != null) {
            metaService.recalcularTodasMetasCelula(salvo.getCelula().getId());
        }

        return toDTO(salvo);
    }

    // =========================
    // DELETAR (soft delete)
    // =========================
    @Transactional
    public void deletar(Long id) {
        Visitante visitante = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));
        visitante.setAtivo(false);
        repository.save(visitante);

        if (visitante.getCelula() != null) {
            metaService.recalcularTodasMetasCelula(visitante.getCelula().getId());
        }
    }

    // =========================
    // LISTAR POR CÉLULA (exclui arquivados)
    // =========================
    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarVisitantesPorCelula(Long celulaId) {
        return repository.findByCelulaIdAndAtivoTrueAndArquivadoFalse(celulaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarAtivosPorCelula(Long celulaId) {
        return repository.findByCelulaIdAndAtivoTrueAndArquivadoFalse(celulaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    @Transactional(readOnly = true)
    public VisitanteResponseDTO buscarPorId(Long id) {
        Visitante visitante = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));
        return toDTO(visitante);
    }

    // =========================
    // DECISÃO ATUAL
    // =========================
    @Transactional(readOnly = true)
    public HistoricoDecisaoDTO buscarDecisaoAtual(Long id) {
        Visitante v = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));
        return new HistoricoDecisaoDTO(
                v.getId(),
                v.getNome(),
                v.getDecisaoEspiritual() != null ? v.getDecisaoEspiritual().name() : null
        );
    }

    // =========================
    // ARQUIVAR
    // =========================
    @Transactional
    public void arquivar(Long id) {
        Visitante visitante = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

        visitante.setArquivado(true);
        visitante.setDataArquivamento(LocalDate.now());
        repository.save(visitante);

        // Motivo derivado da decisão espiritual — sem campo extra
        String motivo = visitante.getDecisaoEspiritual() != null
                ? visitante.getDecisaoEspiritual().name()
                : "SEM_DECISAO";

        auditoria.registrar("VISITANTE", visitante.getId(), visitante.getNome(), "ARQUIVAR",
                Map.of("motivo", Map.of("para", motivo))
        );

        if (visitante.getCelula() != null) {
            metaService.recalcularTodasMetasCelula(visitante.getCelula().getId());
        }
    }

    // =========================
    // DESARQUIVAR
    // =========================
    @Transactional
    public void desarquivar(Long id) {
        Visitante visitante = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

        visitante.setArquivado(false);
        visitante.setDataArquivamento(null);
        repository.save(visitante);

        auditoria.registrar("VISITANTE", visitante.getId(), visitante.getNome(), "DESARQUIVAR",
                Map.of()
        );

        if (visitante.getCelula() != null) {
            metaService.recalcularTodasMetasCelula(visitante.getCelula().getId());
        }
    }

    // =========================
    // LISTAR ARQUIVADOS
    // =========================
    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarArquivados() {
        return repository.findByArquivadoTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // AUXILIARES PRIVADOS
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
        dto.setArquivado(visitante.isArquivado());
        dto.setDataArquivamento(visitante.getDataArquivamento());
        dto.setDecisaoEspiritual(
                visitante.getDecisaoEspiritual() != null
                        ? visitante.getDecisaoEspiritual().name()
                        : "NENHUMA"
        );
        dto.setMotivoArquivamento(
                visitante.isArquivado()
                        ? (visitante.getDecisaoEspiritual() != null
                           ? visitante.getDecisaoEspiritual().name()
                           : "SEM_DECISAO")
                        : null
        );
        dto.setCelula(visitante.getCelula() != null ? visitante.getCelula().getNome() : null); // ← só isso
        return dto;
    }
    @Transactional(readOnly = true)
    public Page<VisitanteResponseDTO> listarPaginado(Pageable pageable) {
        return repository.findAllByArquivadoFalse(pageable).map(this::toDTO);
    }

    // =========================
// LISTAR ARQUIVADOS (paginado)
// =========================
    @Transactional(readOnly = true)
    public Page<VisitanteResponseDTO> listarArquivadosPaginado(Pageable pageable) {
        return repository.findByArquivadoTrue(pageable).map(this::toDTO);
    }
}