package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.EncontroMissao70RequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.Missao70RequestDTO;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;
import com.gestaoigrejaemcelula.demo.domain.entity.*;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class Missao70Service {

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
        missao.setDataInicio(dto.getDataInicio());
        missao.setEncontrosRestantes(4);
        missao.setProximaSemana(1);
        missao.setStatus(StatusMissao70.EM_ANDAMENTO);

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new RuntimeException("Célula não encontrada"));
            missao.setCelula(celula);
        }
        if (dto.getLiderId() != null) {
            missao.setLider(membroRepository.findById(dto.getLiderId())
                    .orElseThrow(() -> new RuntimeException("Líder não encontrado")));
        }
        if (dto.getAuxiliarId() != null) {
            missao.setAuxiliar(membroRepository.findById(dto.getAuxiliarId())
                    .orElseThrow(() -> new RuntimeException("Auxiliar não encontrado")));
        }

        return missao70Repository.save(missao);
    }

    @Transactional
    public Missao70 atualizar(Long id, Missao70RequestDTO dto) {
        Missao70 missao = missao70Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Missão 70 não encontrada"));

        missao.setNome(dto.getNome());
        missao.setNomeAnfitriao(dto.getNomeAnfitriao());
        missao.setEndereco(dto.getEndereco());
        missao.setTelefoneContato(dto.getTelefoneContato());
        missao.setDataInicio(dto.getDataInicio());

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new RuntimeException("Célula não encontrada"));
            missao.setCelula(celula);
        } else {
            missao.setCelula(null);
        }
        if (dto.getLiderId() != null) {
            missao.setLider(membroRepository.findById(dto.getLiderId())
                    .orElseThrow(() -> new RuntimeException("Líder não encontrado")));
        } else {
            missao.setLider(null);
        }
        if (dto.getAuxiliarId() != null) {
            missao.setAuxiliar(membroRepository.findById(dto.getAuxiliarId())
                    .orElseThrow(() -> new RuntimeException("Auxiliar não encontrado")));
        } else {
            missao.setAuxiliar(null);
        }

        return missao70Repository.save(missao);
    }

    @Transactional
    public Missao70 cancelar(Long id) {
        Missao70 missao = buscarPorId(id);
        missao.setStatus(StatusMissao70.CANCELADA);
        return missao70Repository.save(missao);
    }

    // ─────────────────────────────────────────────────────────────
    // VISITANTES
    // ─────────────────────────────────────────────────────────────

    @Transactional
    public Missao70 adicionarVisitante(Long missaoId, Long visitanteId) {
        Missao70 missao = buscarPorId(missaoId);
        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

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
            throw new RuntimeException("Esta Missão 70 não está em andamento.");
        }
        if (missao.getEncontrosRestantes() <= 0) {
            throw new RuntimeException("Todas as 4 semanas já foram realizadas.");
        }

        int semanaAtual = missao.getProximaSemana();

        // ── Cria o encontro ──────────────────────────────────────
        EncontroMissao70 encontro = new EncontroMissao70();
        encontro.setDataEncontro(dto.getDataEncontro());
        encontro.setNumeroSemana(semanaAtual);
        encontro.setObservacoes(dto.getObservacoes());
        encontro.setMissao70(missao);

        boolean houveDecisao = false;

        if (dto.getDecisoes() != null) {
            for (EncontroMissao70RequestDTO.DecisaoDTO decisaoDTO : dto.getDecisoes()) {
                Visitante visitante = visitanteRepository.findById(decisaoDTO.getVisitanteId())
                        .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

                // Registra a decisão no encontro
                DecisaoMissao70 decisao = new DecisaoMissao70();
                decisao.setTipoDecisao(decisaoDTO.getTipoDecisao());
                decisao.setVisitante(visitante);
                decisao.setEncontro(encontro);
                encontro.getDecisoes().add(decisao);

                // Atualiza o campo decisaoEspiritual do Visitante
                // (só altera se ainda não tem uma decisão registrada)
                DecisaoEspiritual atual = visitante.getDecisaoEspiritual();
                boolean jaTemDecisao = atual == DecisaoEspiritual.ACEITOU_JESUS
                        || atual == DecisaoEspiritual.RECONCILIOU
                        || atual == DecisaoEspiritual.BATISMO_AGUAS;

                if (!jaTemDecisao) {
                    visitante.setDecisaoEspiritual(decisaoDTO.getTipoDecisao());

                    // Garante vínculo com a célula para que o recálculo funcione
                    if (visitante.getCelula() == null && missao.getCelula() != null) {
                        visitante.setCelula(missao.getCelula());
                    }

                    visitanteRepository.save(visitante);
                    houveDecisao = true;
                }
            }
        }

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
        // Garante que o progresso das metas seja atualizado no banco
        // mesmo que o frontend não dispare o endpoint /recalcular
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
    public Missao70 buscarPorId(Long id) {
        return missao70Repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new RuntimeException("Missão 70 não encontrada"));
    }
}