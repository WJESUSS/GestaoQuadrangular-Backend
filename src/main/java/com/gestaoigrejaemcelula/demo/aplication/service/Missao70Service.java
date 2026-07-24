package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.enums.MotivoCancelamentoMissao70;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;
import com.gestaoigrejaemcelula.demo.domain.entity.Missao70;
import com.gestaoigrejaemcelula.demo.domain.entity.EncontroMissao70;
import com.gestaoigrejaemcelula.demo.domain.entity.DecisaoMissao70;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class Missao70Service {

    private static final String MSG_MISSAO70_NAO_ENCONTRADA = "MSG_MISSAO70_NAO_ENCONTRADA";

    private final Missao70Repository missao70Repository;
    private final EncontroMissao70Repository encontroRepository;
    private final CelulaRepository celulaRepository;
    private final MembroRepository membroRepository;
    private final VisitanteRepository visitanteRepository;
    private final MetaService metaService; // ← injeção para recalcular metas

    public Missao70Service(Missao70Repository missao70Repository,
                           EncontroMissao70Repository encontroRepository,
                           CelulaRepository celulaRepository,
                           MembroRepository membroRepository,
                           VisitanteRepository visitanteRepository,
                           MetaService metaService) {
        this.missao70Repository = missao70Repository;
        this.encontroRepository = encontroRepository;
        this.celulaRepository   = celulaRepository;
        this.membroRepository   = membroRepository;
        this.visitanteRepository = visitanteRepository;
        this.metaService        = metaService;
    }

    // ─────────────────────────────────────────────────────────────
    // CRUD
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Missao70 criar(Missao70RequestDTO dto) {
        Missao70 missao = new Missao70();
        missao.setNome(dto.getNome());
        missao.setNomeAnfitriao(dto.getNomeAnfitriao());
        missao.setEndereco(dto.getEndereco());
        missao.setTelefoneContato(dto.getTelefoneContato());
        missao.setHorario(dto.getHorario());          // ⬅️ NOVO — grava o horário fixo dos cultos
        missao.setDataInicio(dto.getDataInicio());
        missao.setEncontrosRestantes(4);
        missao.setProximaSemana(1);
        missao.setStatus(StatusMissao70.EM_ANDAMENTO);

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada"));
            missao.setCelula(celula);
        }
        if (dto.getLiderId() != null) {
            missao.setLider(membroRepository.findById(dto.getLiderId())
                    .orElseThrow(() -> new EntityNotFoundException("Líder não encontrado")));
        }
        if (dto.getAuxiliarId() != null) {
            missao.setAuxiliar(membroRepository.findById(dto.getAuxiliarId())
                    .orElseThrow(() -> new EntityNotFoundException("Auxiliar não encontrado")));
        }
        if (dto.getTerceiroMembroId() != null) {
            missao.setTerceiroMembro(membroRepository.findById(dto.getTerceiroMembroId())
                    .orElseThrow(() -> new EntityNotFoundException("Terceiro membro não encontrado")));
        }

        return missao70Repository.save(missao);
    }

    @Transactional
    public Missao70 atualizar(Long id, Missao70RequestDTO dto) {
        Missao70 missao = missao70Repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MSG_MISSAO70_NAO_ENCONTRADA));

        missao.setNome(dto.getNome());
        missao.setNomeAnfitriao(dto.getNomeAnfitriao());
        missao.setEndereco(dto.getEndereco());
        missao.setTelefoneContato(dto.getTelefoneContato());
        missao.setHorario(dto.getHorario());          // ⬅️ NOVO — permite editar o horário fixo depois
        missao.setDataInicio(dto.getDataInicio());

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada"));
            missao.setCelula(celula);
        } else {
            missao.setCelula(null);
        }
        if (dto.getLiderId() != null) {
            missao.setLider(membroRepository.findById(dto.getLiderId())
                    .orElseThrow(() -> new EntityNotFoundException("Líder não encontrado")));
        } else {
            missao.setLider(null);
        }
        if (dto.getAuxiliarId() != null) {
            missao.setAuxiliar(membroRepository.findById(dto.getAuxiliarId())
                    .orElseThrow(() -> new EntityNotFoundException("Auxiliar não encontrado")));
        } else {
            missao.setAuxiliar(null);
        }
        if (dto.getTerceiroMembroId() != null) {
            missao.setTerceiroMembro(membroRepository.findById(dto.getTerceiroMembroId())
                    .orElseThrow(() -> new EntityNotFoundException("Terceiro membro não encontrado")));
        } else {
            missao.setTerceiroMembro(null);
        }

        return missao70Repository.save(missao);
    }

    @Transactional
    public Missao70ResponseDTO cancelar(Long id, CancelarMissao70RequestDTO dto) {
        Missao70 missao = missao70Repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new EntityNotFoundException("Casa da Missão 70 não encontrada."));

        if (dto.getMotivoCancelamento() == null) {
            throw new IllegalArgumentException("Informe o motivo do cancelamento.");
        }
        if (dto.getMotivoCancelamento() == MotivoCancelamentoMissao70.OUTRO
                && (dto.getObservacaoCancelamento() == null || dto.getObservacaoCancelamento().isBlank())) {
            throw new IllegalArgumentException("Descreva o motivo em 'Outro'.");
        }

        missao.setStatus(StatusMissao70.CANCELADA);
        missao.setMotivoCancelamento(dto.getMotivoCancelamento());
        missao.setObservacaoCancelamento(dto.getObservacaoCancelamento());

        missao70Repository.save(missao);
        return Missao70ResponseDTO.de(missao);
    }

    // ─────────────────────────────────────────────────────────────
    // VISITANTES
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Missao70 adicionarVisitante(Long missaoId, Long visitanteId) {
        Missao70 missao = buscarPorId(missaoId);
        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new EntityNotFoundException("Visitante não encontrado"));

        if (!missao.getVisitantes().contains(visitante)) {
            missao.getVisitantes().add(visitante);

            // Garante que o visitante conhece a célula da missão
            // (necessário para que countByCelulaIdAndDecisaoEspiritual funcione)
            if (visitante.getCelula() == null && missao.getCelula() != null) {
                visitante.setCelula(missao.getCelula());
                visitanteRepository.save(visitante);
            }
        }

        return missao70Repository.save(missao);
    }

    // ─────────────────────────────────────────────────────────────
    // ENCONTROS
    // ─────────────────────────────────────────────────────────────

    /**
     * Registra o encontro da semana atual.
     * Semana calculada AUTOMATICAMENTE: 1 → 2 → 3 → 4 → CONCLUÍDA
     */
    @Transactional
    public Map<String, Object> registrarEncontro(Long missaoId, EncontroMissao70RequestDTO dto) {
        Missao70 missao = buscarPorId(missaoId);

        if (missao.getStatus() != StatusMissao70.EM_ANDAMENTO) {
            throw new IllegalStateException("Esta Missão 70 não está em andamento.");
        }
        if (missao.getEncontrosRestantes() <= 0) {
            throw new IllegalStateException("Todas as 4 semanas já foram realizadas.");
        }

        int semanaAtual = missao.getProximaSemana();

        // ── Cria o encontro ──────────────────────────────────────
        EncontroMissao70 encontro = new EncontroMissao70();
        encontro.setDataEncontro(dto.getDataEncontro());
        encontro.setHoraEncontro(dto.getHoraEncontro() != null ? dto.getHoraEncontro() : missao.getHorario());
        encontro.setNumeroSemana(semanaAtual);
        encontro.setObservacoes(dto.getObservacoes());
        encontro.setMissao70(missao);

        // ── Registra quem esteve presente neste culto ─────────────
        if (dto.getVisitantesPresentesIds() != null && !dto.getVisitantesPresentesIds().isEmpty()) {
            List<Visitante> presentes = visitanteRepository.findAllById(dto.getVisitantesPresentesIds());
            encontro.setVisitantesPresentes(presentes);
        }

        boolean houveDecisao = processarDecisoes(missao, encontro, dto);

        encontroRepository.save(encontro);

        // ── Atualiza contadores da missão ────────────────────────
        missao.setProximaSemana(semanaAtual + 1);
        missao.setEncontrosRestantes(missao.getEncontrosRestantes() - 1);

        boolean concluida = missao.getEncontrosRestantes() == 0;
        if (concluida) {
            missao.setStatus(StatusMissao70.CONCLUIDA);
        }

        missao70Repository.save(missao);

        // ── Recalcula metas da célula se houve decisão espiritual ─
        if (houveDecisao && missao.getCelula() != null) {
            metaService.recalcularTodasMetasCelula(missao.getCelula().getId());
        }

        return Map.of(
                "semanaRegistrada", semanaAtual,
                "semanasRestantes", missao.getEncontrosRestantes(),
                "concluida",        concluida,
                "mensagem",         concluida
                        ? "Parabéns! Missão 70 concluída — 4 semanas realizadas!"
                        : "Semana " + semanaAtual + " de 4 registrada com sucesso."
        );
    }

    private boolean processarDecisoes(Missao70 missao, EncontroMissao70 encontro,
                                       EncontroMissao70RequestDTO dto) {
        if (dto.getDecisoes() == null || dto.getDecisoes().isEmpty()) {
            return false;
        }

        List<Long> visitanteIds = dto.getDecisoes().stream()
                .map(EncontroMissao70RequestDTO.DecisaoDTO::getVisitanteId)
                .toList();
        List<Visitante> visitantesDecisao = visitanteRepository.findAllById(visitanteIds);
        var visitanteMap = visitantesDecisao.stream()
                .collect(Collectors.toMap(Visitante::getId, v -> v));

        boolean houveDecisao = false;

        for (EncontroMissao70RequestDTO.DecisaoDTO decisaoDTO : dto.getDecisoes()) {
            Visitante visitante = visitanteMap.get(decisaoDTO.getVisitanteId());
            if (visitante == null) {
                throw new EntityNotFoundException("Visitante não encontrado: " + decisaoDTO.getVisitanteId());
            }

            DecisaoEspiritual novaDecisao = decisaoDTO.getTipoDecisao();
            boolean decisaoMudou = visitante.getDecisaoEspiritual() != novaDecisao;

            if (decisaoMudou) {
                DecisaoMissao70 decisao = new DecisaoMissao70();
                decisao.setTipoDecisao(novaDecisao);
                decisao.setVisitante(visitante);
                decisao.setEncontro(encontro);
                encontro.getDecisoes().add(decisao);

                visitante.setDecisaoEspiritual(novaDecisao);

                if (visitante.getCelula() == null && missao.getCelula() != null) {
                    visitante.setCelula(missao.getCelula());
                }

                visitanteRepository.save(visitante);
                houveDecisao = true;
            }
        }

        return houveDecisao;
    }

    /**
     * Lista o histórico de cultos (encontros) já registrados nesta casa,
     * em ordem de semana (1, 2, 3, 4).
     */
    @Transactional(readOnly = true)
    public List<EncontroMissao70ResponseDTO> listarEncontros(Long missaoId) {
        buscarPorId(missaoId); // garante que a casa existe (lança 404 se não)
        return List.copyOf(encontroRepository.findByMissao70IdOrderByNumeroSemanaAsc(missaoId)
                .stream()
                .map(EncontroMissao70ResponseDTO::de)
                .toList());
    }

    /**
     * Edita um culto já registrado (data, horário, observações e/ou presença).
     * Não mexe no número da semana nem nos contadores da missão.
     */
    @Transactional
    public EncontroMissao70 atualizarEncontro(Long missaoId, Long encontroId, EncontroMissao70RequestDTO dto) {
        EncontroMissao70 encontro = encontroRepository.findById(encontroId)
                .orElseThrow(() -> new EntityNotFoundException("Culto não encontrado"));

        if (!encontro.getMissao70().getId().equals(missaoId)) {
            throw new IllegalStateException("Este culto não pertence a esta casa.");
        }

        if (dto.getDataEncontro() != null) {
            encontro.setDataEncontro(dto.getDataEncontro());
        }
        if (dto.getHoraEncontro() != null) {              // ⬅️ NOVO — permite corrigir o horário na edição
            encontro.setHoraEncontro(dto.getHoraEncontro());
        }
        if (dto.getObservacoes() != null) {
            encontro.setObservacoes(dto.getObservacoes());
        }
        if (dto.getVisitantesPresentesIds() != null) {
            List<Visitante> presentes = visitanteRepository.findAllById(dto.getVisitantesPresentesIds());
            encontro.setVisitantesPresentes(presentes);
        }

        return encontroRepository.save(encontro);
    }

    // ─────────────────────────────────────────────────────────────
    // DECISÃO DO VISITANTE
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public void alterarDecisaoVisitante(Long missaoId, Long visitanteId, AlterarDecisaoVisitanteDTO dto) {
        Missao70 missao = missao70Repository.findByIdWithAssociations(missaoId)
                .orElseThrow(() -> new EntityNotFoundException(MSG_MISSAO70_NAO_ENCONTRADA));

        boolean visitantePertence = missao.getVisitantes().stream()
                .anyMatch(v -> v.getId().equals(visitanteId));
        if (!visitantePertence) {
            throw new IllegalStateException("Visitante não pertence a esta Missão 70");
        }

        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new EntityNotFoundException("Visitante não encontrado"));

        visitante.setDecisaoEspiritual(dto.getTipoDecisao());

        if (visitante.getCelula() == null && missao.getCelula() != null) {
            visitante.setCelula(missao.getCelula());
        }

        visitanteRepository.save(visitante);

        if (missao.getCelula() != null) {
            metaService.recalcularTodasMetasCelula(missao.getCelula().getId());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // CONSULTAS
    // ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Missao70> listarTodas() {
        return missao70Repository.findAllWithAssociations();
    }

    @Transactional(readOnly = true)
    public List<Missao70> listarPorCelula(Long celulaId) {
        return missao70Repository.findByCelulaId(celulaId);
    }

    @Transactional(readOnly = true)
    public Page<Missao70ResponseDTO> listarTodasPaginado(Pageable pageable) {
        return missao70Repository.findAllWithAssociationsPaginado(pageable)
                .map(Missao70ResponseDTO::de);
    }

    @Transactional(readOnly = true)
    public Page<Missao70ResponseDTO> listarPorCelulaPaginado(Long celulaId, Pageable pageable) {
        return missao70Repository.findByCelulaIdPaginado(celulaId, pageable)
                .map(Missao70ResponseDTO::de);
    }

    @Transactional(readOnly = true)
    public Page<Missao70ResponseDTO> listarPorStatusPaginado(StatusMissao70 status, Pageable pageable) {
        return missao70Repository.findByStatusPaginado(status, pageable)
                .map(Missao70ResponseDTO::de);
    }

    @Transactional(readOnly = true)
    public Page<Missao70ResponseDTO> listarPorCelulaEStatusPaginado(Long celulaId, StatusMissao70 status, Pageable pageable) {
        return missao70Repository.findByCelulaIdAndStatusPaginado(celulaId, status, pageable)
                .map(Missao70ResponseDTO::de);
    }

    @Transactional(readOnly = true)
    public Missao70 buscarPorId(Long id) {
        return missao70Repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new EntityNotFoundException(MSG_MISSAO70_NAO_ENCONTRADA));
    }
}