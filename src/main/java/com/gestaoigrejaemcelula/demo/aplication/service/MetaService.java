package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.MetaRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MetaResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Meta;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;

import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MetaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.VisitanteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class MetaService {

    private final MetaRepository metaRepository;
    private final CelulaRepository celulaRepository;
    private final VisitanteRepository visitanteRepository;

    public MetaService(MetaRepository metaRepository,
                       CelulaRepository celulaRepository,
                       VisitanteRepository visitanteRepository) {
        this.metaRepository = metaRepository;
        this.celulaRepository = celulaRepository;
        this.visitanteRepository = visitanteRepository;
    }

    // ─────────────────────────────────────────────────────────────
    // CRUD
    // ─────────────────────────────────────────────────────────────

    public MetaResponseDTO criarMeta(MetaRequestDTO dto) {
        Celula celula = celulaRepository.findById(dto.getCelulaId())
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        Meta meta = new Meta();
        meta.setCelula(celula);
        meta.setTipoMeta(dto.getTipoMeta());
        meta.setMetaTotal(dto.getMetaTotal());
        meta.setMetaAlcancada(0);
        meta.setMesAno(dto.getMesAno());
        meta.setAtiva(dto.isAtiva());
        meta.setDescricao(dto.getDescricao());
        meta.setDataCriacao(LocalDate.now());

        // Já sincroniza o progresso inicial com os visitantes existentes
        sincronizarProgresso(meta, celula.getId());

        return new MetaResponseDTO(metaRepository.save(meta));
    }

    public MetaResponseDTO buscarPorId(Long id) {
        return new MetaResponseDTO(buscarEntidade(id));
    }

    public List<MetaResponseDTO> listarPorCelula(Long celulaId) {
        return metaRepository.findByCelulaIdOrderByMesAnoDesc(celulaId)
                .stream().map(MetaResponseDTO::new).toList();
    }

    public List<MetaResponseDTO> listarAtivasPorCelula(Long celulaId) {
        return metaRepository.findByCelulaIdAndAtivaOrderByMesAnoDesc(celulaId, true)
                .stream().map(MetaResponseDTO::new).toList();
    }

    public MetaResponseDTO atualizar(Long id, MetaRequestDTO dto) {
        if (dto.getCelulaId() == null)
            throw new IllegalArgumentException("O campo celulaId é obrigatório.");

        Meta meta = buscarEntidade(id);
        meta.setTipoMeta(dto.getTipoMeta());
        meta.setMetaTotal(dto.getMetaTotal());
        meta.setMesAno(dto.getMesAno());
        meta.setAtiva(dto.isAtiva());
        meta.setDescricao(dto.getDescricao());
        return new MetaResponseDTO(metaRepository.save(meta));
    }

    public void deletar(Long id) {
        metaRepository.delete(buscarEntidade(id));
    }

    // ─────────────────────────────────────────────────────────────
    // PROGRESSO MANUAL
    // ─────────────────────────────────────────────────────────────

    public MetaResponseDTO incrementarProgresso(Long metaId) {
        Meta meta = buscarEntidade(metaId);
        meta.incrementarProgresso();
        return new MetaResponseDTO(metaRepository.save(meta));
    }

    public MetaResponseDTO decrementarProgresso(Long metaId) {
        Meta meta = buscarEntidade(metaId);
        meta.decrementarProgresso();
        return new MetaResponseDTO(metaRepository.save(meta));
    }

    // ─────────────────────────────────────────────────────────────
    // SINCRONIZAÇÃO AUTOMÁTICA — chamada pelo endpoint /recalcular
    // ─────────────────────────────────────────────────────────────

    /**
     * Recalcula TODAS as metas ativas de uma célula com base nas
     * decisões espirituais registradas nos visitantes.
     *
     * Chamado sempre que um relatório, Missão 70 ou Casa de Paz
     * registra uma decisão espiritual.
     */
    public void recalcularTodasMetasCelula(Long celulaId) {
        List<Meta> metas = metaRepository.findByCelulaIdAndAtivaOrderByMesAnoDesc(celulaId, true);
        if (metas.isEmpty()) return;

        // Query agregada única para não fazer N count queries
        List<DecisaoEspiritual> decisoesMapeadas = metas.stream()
                .map(m -> mapearTipoMetaParaDecisao(m.getTipoMeta()))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (!decisoesMapeadas.isEmpty()) {
            List<Object[]> contagens = visitanteRepository.countPorDecisao(celulaId, decisoesMapeadas);
            Map<DecisaoEspiritual, Integer> mapaContagens = new java.util.HashMap<>();
            for (Object[] row : contagens) {
                mapaContagens.put((DecisaoEspiritual) row[0], ((Number) row[1]).intValue());
            }
            for (Meta meta : metas) {
                DecisaoEspiritual decisao = mapearTipoMetaParaDecisao(meta.getTipoMeta());
                if (decisao != null) {
                    meta.setMetaAlcancada(mapaContagens.getOrDefault(decisao, 0));
                }
            }
        }

        metaRepository.saveAll(metas);
    }

    /**
     * Sincroniza o progresso de uma única meta contra o banco de visitantes.
     * Usado tanto na criação quanto no recálculo em lote.
     */
    public MetaResponseDTO atualizarProgressoAutomatico(Long metaId) {
        Meta meta = buscarEntidade(metaId);
        sincronizarProgresso(meta, meta.getCelula().getId());
        return new MetaResponseDTO(metaRepository.save(meta));
    }

    // ─────────────────────────────────────────────────────────────
    // ALERTAS
    // ─────────────────────────────────────────────────────────────

    public List<MetaResponseDTO> buscarMetasProximasConclusao(Long celulaId) {
        return metaRepository.encontrarMetasProximasConclusao(celulaId)
                .stream().map(MetaResponseDTO::new).toList();
    }

    public List<MetaResponseDTO> buscarMetasEmAtraso(Long celulaId) {
        return metaRepository.encontrarMetasEmAtraso(celulaId)
                .stream().map(MetaResponseDTO::new).toList();
    }

    public long contarMetasConcluidas(Long celulaId) {
        return metaRepository.contarMetasConcluidas(celulaId);
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ─────────────────────────────────────────────────────────────

    private Meta buscarEntidade(Long id) {
        return metaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada"));
    }

    /**
     * Aplica a contagem de visitantes na meta.
     * Suporta todos os tipos: BATISMO, CONVERSAO, RECONCILIACAO, DISCIPULADO.
     *
     * A query countByCelulaIdAndDecisaoEspiritual conta visitantes cujo
     * campo decisaoEspiritual foi atualizado em qualquer fluxo:
     *   - Relatório semanal da célula
     *   - Encontro de Casa de Paz
     *   - Encontro de Missão 70
     *
     * Portanto o visitante precisa estar com celulaId preenchido.
     * Se o vínculo for feito via Casa de Paz / Missão 70, verifique se
     * o Visitante.celulaId é setado ao adicioná-lo a esses módulos.
     */
    private void sincronizarProgresso(Meta meta, Long celulaId) {
        DecisaoEspiritual decisao = mapearTipoMetaParaDecisao(meta.getTipoMeta());
        if (decisao == null) {
            // DISCIPULADO ou tipo sem mapeamento: não altera automaticamente
            return;
        }

        long contador = visitanteRepository.countByCelulaIdAndDecisaoEspiritualAndAtivoTrue(celulaId, decisao);
        meta.setMetaAlcancada((int) contador);
    }

    /**
     * Mapeia o tipo de meta para a decisão espiritual equivalente no Visitante.
     * Retorna null para tipos sem equivalência direta (ex.: DISCIPULADO).
     */
    private DecisaoEspiritual mapearTipoMetaParaDecisao(String tipoMeta) {
        if (tipoMeta == null) return null;
        return switch (tipoMeta.toUpperCase()) {
            case "BATISMO"        -> DecisaoEspiritual.BATISMO_AGUAS;
            case "CONVERSAO"      -> DecisaoEspiritual.ACEITOU_JESUS;
            case "RECONCILIACAO"  -> DecisaoEspiritual.RECONCILIOU;
            default               -> null; // DISCIPULADO e outros: manual
        };
    }
}