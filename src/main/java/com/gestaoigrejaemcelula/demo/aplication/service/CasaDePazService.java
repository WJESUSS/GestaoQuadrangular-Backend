package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.CasaDePazRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.EncontroRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioCasaDePazDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.*;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusCasaDePaz;
import com.gestaoigrejaemcelula.demo.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CasaDePazService {

    private final CasaDePazRepository casaDePazRepository;
    private final EncontroCasaDePazRepository encontroRepository;
    private final DecisaoEncontroRepository decisaoRepository;
    private final CelulaRepository celulaRepository;
    private final MembroRepository membroRepository;
    private final VisitanteRepository visitanteRepository;
    private final MetaService metaService;

    public CasaDePazService(CasaDePazRepository casaDePazRepository,
                            EncontroCasaDePazRepository encontroRepository,
                            DecisaoEncontroRepository decisaoRepository,
                            CelulaRepository celulaRepository,
                            MembroRepository membroRepository,
                            VisitanteRepository visitanteRepository,
                            MetaService metaService) {
        this.casaDePazRepository = casaDePazRepository;
        this.encontroRepository  = encontroRepository;
        this.decisaoRepository   = decisaoRepository;
        this.celulaRepository    = celulaRepository;
        this.membroRepository    = membroRepository;
        this.visitanteRepository = visitanteRepository;
        this.metaService         = metaService;
    }

    // ─────────────────────────────────────────────────────────────
    // CRUD
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public CasaDePaz criar(CasaDePazRequestDTO dto) {
        Celula celula = celulaRepository.findById(dto.getCelulaId())
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));
        Membro lider = membroRepository.findById(dto.getLiderId())
                .orElseThrow(() -> new RuntimeException("Líder não encontrado"));
        Membro auxiliar = membroRepository.findById(dto.getAuxiliarId())
                .orElseThrow(() -> new RuntimeException("Auxiliar não encontrado"));

        CasaDePaz casa = new CasaDePaz();
        casa.setNome(dto.getNome());
        casa.setNomeAnfitriao(dto.getNomeAnfitriao());
        casa.setEndereco(dto.getEndereco());
        casa.setTelefoneContato(dto.getTelefoneContato());
        casa.setDataInicio(dto.getDataInicio());
        casa.setCelula(celula);
        casa.setLider(lider);
        casa.setAuxiliar(auxiliar);
        casa.setEncontrosRestantes(7);

        return casaDePazRepository.save(casa);
    }

    @Transactional
    public CasaDePaz atualizar(Long id, CasaDePazRequestDTO dto) {
        CasaDePaz casa = casaDePazRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Casa de Paz não encontrada"));
        Celula celula = celulaRepository.findById(dto.getCelulaId())
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));
        Membro lider = membroRepository.findById(dto.getLiderId())
                .orElseThrow(() -> new RuntimeException("Líder não encontrado"));
        Membro auxiliar = membroRepository.findById(dto.getAuxiliarId())
                .orElseThrow(() -> new RuntimeException("Auxiliar não encontrado"));

        casa.setNome(dto.getNome());
        casa.setNomeAnfitriao(dto.getNomeAnfitriao());
        casa.setEndereco(dto.getEndereco());
        casa.setTelefoneContato(dto.getTelefoneContato());
        casa.setDataInicio(dto.getDataInicio());
        casa.setCelula(celula);
        casa.setLider(lider);
        casa.setAuxiliar(auxiliar);

        return casaDePazRepository.save(casa);
    }

    @Transactional
    public CasaDePaz cancelar(Long casaId) {
        CasaDePaz casa = buscarPorId(casaId);
        casa.setStatus(StatusCasaDePaz.CANCELADA);
        return casaDePazRepository.save(casa);
    }

    // ─────────────────────────────────────────────────────────────
    // VISITANTES
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public CasaDePaz adicionarVisitante(Long casaId, Long visitanteId) {
        CasaDePaz casa = buscarPorId(casaId);
        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

        if (!casa.getVisitantes().contains(visitante)) {
            casa.getVisitantes().add(visitante);

            if (visitante.getCelula() == null) {
                visitante.setCelula(casa.getCelula());
                visitanteRepository.save(visitante);
            }
        }
        return casaDePazRepository.save(casa);
    }

    // ─────────────────────────────────────────────────────────────
    // ENCONTROS
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> registrarEncontro(Long casaId, EncontroRequestDTO dto) {
        CasaDePaz casa = buscarPorId(casaId);

        if (casa.getStatus() != StatusCasaDePaz.EM_ANDAMENTO) {
            throw new RuntimeException("Esta Casa de Paz não está em andamento.");
        }
        if (casa.getEncontrosRestantes() <= 0) {
            throw new RuntimeException("Todos os encontros já foram realizados.");
        }

        // ── Validação: impede dois encontros na mesma data ───────
        boolean dataJaRegistrada = encontroRepository
                .existsByCasaDePazIdAndDataEncontro(casaId, dto.getDataEncontro());
        if (dataJaRegistrada) {
            throw new RuntimeException(
                    "Já existe um encontro registrado nesta data: " + dto.getDataEncontro()
                            + ". Escolha outra data.");
        }

        // ── Cria o encontro ──────────────────────────────────────
        EncontroCasaDePaz encontro = new EncontroCasaDePaz();
        encontro.setDataEncontro(dto.getDataEncontro());
        encontro.setObservacoes(dto.getObservacoes());
        encontro.setCasaDePaz(casa);

        boolean houveDecisao = false;

        if (dto.getDecisoes() != null) {
            for (EncontroRequestDTO.DecisaoDTO decisaoDTO : dto.getDecisoes()) {
                Visitante visitante = visitanteRepository.findById(decisaoDTO.getVisitanteId())
                        .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

                DecisaoEncontro decisao = new DecisaoEncontro();
                decisao.setTipoDecisao(decisaoDTO.getTipoDecisao());
                decisao.setVisitante(visitante);
                decisao.setEncontro(encontro);
                encontro.getDecisoes().add(decisao);

                DecisaoEspiritual atual = visitante.getDecisaoEspiritual();
                boolean jaTemDecisao = atual == DecisaoEspiritual.ACEITOU_JESUS
                        || atual == DecisaoEspiritual.RECONCILIOU
                        || atual == DecisaoEspiritual.BATISMO_AGUAS;

                if (!jaTemDecisao) {
                    visitante.setDecisaoEspiritual(decisaoDTO.getTipoDecisao());
                    if (visitante.getCelula() == null) {
                        visitante.setCelula(casa.getCelula());
                    }
                    visitanteRepository.save(visitante);
                    houveDecisao = true;
                }
            }
        }

        encontroRepository.save(encontro);

        // ── Atualiza contadores da casa ──────────────────────────
        casa.setEncontrosRestantes(casa.getEncontrosRestantes() - 1);
        if (casa.getEncontrosRestantes() == 0) {
            casa.setStatus(StatusCasaDePaz.CONCLUIDA);
        }
        casaDePazRepository.save(casa);

        if (houveDecisao) {
            metaService.recalcularTodasMetasCelula(casa.getCelula().getId());
        }

        boolean concluida = casa.getEncontrosRestantes() == 0;
        return Map.of(
                "encontro",           encontro,
                "encontrosRestantes", casa.getEncontrosRestantes(),
                "concluida",          concluida,
                "mensagem",           concluida
                        ? "Parabéns! Você concluiu a Casa de Paz!"
                        : "Encontro registrado com sucesso."
        );
    }

    // ─────────────────────────────────────────────────────────────
    // CONSULTAS
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RelatorioCasaDePazDTO> gerarRelatorio(Long celulaId, StatusCasaDePaz status,
                                                      LocalDate dataInicio, LocalDate dataFim) {
        List<CasaDePaz> casas;

        if (celulaId != null && status != null) {
            casas = casaDePazRepository.findByCelulaIdAndStatus(celulaId, status);
        } else if (celulaId != null) {
            casas = casaDePazRepository.findByCelulaId(celulaId);
        } else if (status != null) {
            casas = casaDePazRepository.findByStatus(status);
        } else {
            casas = casaDePazRepository.findAllWithAssociations();
        }

        if (dataInicio != null && dataFim != null) {
            casas = casas.stream()
                    .filter(c -> !c.getDataInicio().isBefore(dataInicio)
                            && !c.getDataInicio().isAfter(dataFim))
                    .collect(Collectors.toList());
        }

        return casas.stream().map(this::toRelatorioDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CasaDePaz buscarPorId(Long id) {
        return casaDePazRepository.findByIdWithAssociations(id)
                .orElseThrow(() -> new RuntimeException("Casa de Paz não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<CasaDePaz> listarTodas() {
        return casaDePazRepository.findAllWithAssociations();
    }

    @Transactional(readOnly = true)
    public List<CasaDePaz> listarPorCelula(Long celulaId) {
        return casaDePazRepository.findByCelulaId(celulaId);
    }

    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listar() {
        return visitanteRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> buscarPorNome(String nome) {
        return visitanteRepository.findByNomeContainingIgnoreCase(nome)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ─────────────────────────────────────────────────────────────

    private RelatorioCasaDePazDTO toRelatorioDTO(CasaDePaz casa) {
        RelatorioCasaDePazDTO dto = new RelatorioCasaDePazDTO();
        dto.setId(casa.getId());
        dto.setNome(casa.getNome());
        dto.setNomeCelula(casa.getCelula().getNome());
        dto.setNomeLider(casa.getLider().getNome());
        dto.setNomeAuxiliar(casa.getAuxiliar().getNome());
        dto.setStatus(casa.getStatus());
        dto.setEncontrosRestantes(casa.getEncontrosRestantes());
        dto.setEncontrosRealizados(7 - casa.getEncontrosRestantes());
        dto.setTotalVisitantes(casa.getVisitantes().size());
        dto.setTotalAceitouJesus(
                decisaoRepository.countByEncontro_CasaDePaz_IdAndTipoDecisao(
                        casa.getId(), DecisaoEspiritual.ACEITOU_JESUS));
        dto.setTotalReconciliacao(
                decisaoRepository.countByEncontro_CasaDePaz_IdAndTipoDecisao(
                        casa.getId(), DecisaoEspiritual.RECONCILIOU));
        dto.setTotalDesejoBatismo(
                decisaoRepository.countByEncontro_CasaDePaz_IdAndTipoDecisao(
                        casa.getId(), DecisaoEspiritual.BATISMO_AGUAS));
        return dto;
    }

    private VisitanteResponseDTO toDTO(Visitante visitante) {
        return new VisitanteResponseDTO(visitante);
    }
}