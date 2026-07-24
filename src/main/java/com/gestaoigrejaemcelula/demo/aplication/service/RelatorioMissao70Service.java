package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioMissao70DTO;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;
import com.gestaoigrejaemcelula.demo.domain.entity.Missao70;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;

import com.gestaoigrejaemcelula.demo.domain.repository.DecisaoMissao70Repository;
import com.gestaoigrejaemcelula.demo.domain.repository.Missao70Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RelatorioMissao70Service {

    private final Missao70Repository missao70Repository;
    private final DecisaoMissao70Repository decisaoRepository;

    public RelatorioMissao70Service(Missao70Repository missao70Repository,
                                    DecisaoMissao70Repository decisaoRepository) {
        this.missao70Repository = missao70Repository;
        this.decisaoRepository = decisaoRepository;
    }

    /**
     * Relatório geral para o pastor.
     * Filtros opcionais: celulaId, status, dataInicio, dataFim.
     */
    @Transactional(readOnly = true)
    public List<RelatorioMissao70DTO> gerarRelatorio(Long celulaId,
                                                     StatusMissao70 status,
                                                     LocalDate dataInicio,
                                                     LocalDate dataFim) {
        List<Missao70> missoes;

        if (celulaId != null && status != null) {
            missoes = missao70Repository.findByCelulaIdAndStatus(celulaId, status);
        } else if (celulaId != null) {
            missoes = missao70Repository.findByCelulaId(celulaId);
        } else if (status != null) {
            missoes = missao70Repository.findByStatus(status);
        } else {
            missoes = missao70Repository.findAllWithAssociations();
        }

        // Filtro por período de dataInicio
        if (dataInicio != null && dataFim != null) {
            missoes = missoes.stream()
                    .filter(m -> !m.getDataInicio().isBefore(dataInicio)
                            && !m.getDataInicio().isAfter(dataFim))
                    .toList();
        }

        return missoes.stream()
                .map(this::toRelatorioDTO)
                .toList();
    }

    /**
     * Relatório de uma única Missão 70 (detalhado).
     */
    @Transactional(readOnly = true)
    public RelatorioMissao70DTO gerarRelatorioPorId(Long id) {
        Missao70 missao = missao70Repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new RuntimeException("Missão 70 não encontrada"));
        return toRelatorioDTO(missao);
    }

    // ── Resumo geral: totais de todas as missões ──────────────────────────────

    @Transactional(readOnly = true)
    public ResumoGeralMissao70 gerarResumoGeral() {
        List<Missao70> todas = missao70Repository.findAllWithAssociations();

        long emAndamento = todas.stream().filter(m -> m.getStatus() == StatusMissao70.EM_ANDAMENTO).count();
        long concluidas  = todas.stream().filter(m -> m.getStatus() == StatusMissao70.CONCLUIDA).count();
        long canceladas  = todas.stream().filter(m -> m.getStatus() == StatusMissao70.CANCELADA).count();

        long totalVisitantes  = todas.stream().mapToLong(m -> m.getVisitantes().size()).sum();
        long totalVisitantesPorCulto = todas.stream()
                .mapToLong(m -> m.getEncontros().stream()
                        .mapToLong(e -> e.getVisitantesPresentes().size())
                        .sum())
                .sum();
        long totalAceitouJesus = todas.stream().mapToLong(m ->
                decisaoRepository.countByEncontro_Missao70_IdAndTipoDecisao(m.getId(), DecisaoEspiritual.ACEITOU_JESUS)
        ).sum();
        long totalReconciliacao = todas.stream().mapToLong(m ->
                decisaoRepository.countByEncontro_Missao70_IdAndTipoDecisao(m.getId(), DecisaoEspiritual.RECONCILIOU)
        ).sum();
        long totalBatismo = todas.stream().mapToLong(m ->
                decisaoRepository.countByEncontro_Missao70_IdAndTipoDecisao(m.getId(), DecisaoEspiritual.BATISMO_AGUAS)
        ).sum();

        return new ResumoGeralMissao70(
                todas.size(), emAndamento, concluidas, canceladas,
                totalVisitantes, totalVisitantesPorCulto,
                totalAceitouJesus, totalReconciliacao, totalBatismo
        );
    }

    // ── Mapeamento interno ────────────────────────────────────────────────────

    private RelatorioMissao70DTO toRelatorioDTO(Missao70 missao) {
        RelatorioMissao70DTO dto = new RelatorioMissao70DTO();
        dto.setId(missao.getId());
        dto.setNome(missao.getNome());
        dto.setNomeAnfitriao(missao.getNomeAnfitriao());
        dto.setEndereco(missao.getEndereco());
        dto.setDataInicio(missao.getDataInicio());
        dto.setStatus(missao.getStatus());

        // Motivo do cancelamento — só é relevante quando status == CANCELADA,
        // mas preenchemos sempre que a entidade tiver o valor (fica null quando
        // a missão nunca foi cancelada, que é o comportamento correto).
        dto.setMotivoCancelamento(missao.getMotivoCancelamento());
        dto.setMotivoCancelamentoDescricao(
                missao.getMotivoCancelamento() != null ? missao.getMotivoCancelamento().getDescricao() : null
        );
        dto.setObservacaoCancelamento(missao.getObservacaoCancelamento());

        if (missao.getCelula() != null) {
            dto.setNomeCelula(missao.getCelula().getNome());
        }
        if (missao.getLider() != null) {
            dto.setNomeLider(missao.getLider().getNome());
        }
        if (missao.getAuxiliar() != null) {
            dto.setNomeAuxiliar(missao.getAuxiliar().getNome());
        }

        if (missao.getTerceiroMembro() != null) {
            dto.setNomeTerceiroMembro(missao.getTerceiroMembro().getNome());
        }

        int semanasRealizadas = 4 - missao.getEncontrosRestantes();
        dto.setSemanasRealizadas(semanasRealizadas);
        dto.setSemanasRestantes(missao.getEncontrosRestantes());
        dto.setSemanaAtual(missao.getProximaSemana());

        dto.setTotalVisitantes(missao.getVisitantes().size());

        // Soma de presenças por culto: mesma pessoa conta 1x em cada semana que participou
        int totalVisitantesPorCulto = missao.getEncontros().stream()
                .mapToInt(e -> e.getVisitantesPresentes().size())
                .sum();
        dto.setTotalVisitantesPorCulto(totalVisitantesPorCulto);

        dto.setTotalAceitouJesus(
                decisaoRepository.countByEncontro_Missao70_IdAndTipoDecisao(
                        missao.getId(), DecisaoEspiritual.ACEITOU_JESUS));
        dto.setTotalReconciliacao(
                decisaoRepository.countByEncontro_Missao70_IdAndTipoDecisao(
                        missao.getId(), DecisaoEspiritual.RECONCILIOU));
        dto.setTotalDesejoBatismo(
                decisaoRepository.countByEncontro_Missao70_IdAndTipoDecisao(
                        missao.getId(), DecisaoEspiritual.BATISMO_AGUAS));

        return dto;
    }

    // ── Classe interna de resumo geral ────────────────────────────────────────

    public static class ResumoGeralMissao70 {
        private final long total;
        private final long emAndamento;
        private final long concluidas;
        private final long canceladas;
        private final long totalVisitantes;
        private final long totalVisitantesPorCulto;
        private final long totalAceitouJesus;
        private final long totalReconciliacao;
        private final long totalBatismo;

        public ResumoGeralMissao70(long total, long emAndamento, long concluidas, long canceladas,
                                   long totalVisitantes, long totalVisitantesPorCulto,
                                   long totalAceitouJesus,
                                   long totalReconciliacao, long totalBatismo) {
            this.total = total;
            this.emAndamento = emAndamento;
            this.concluidas = concluidas;
            this.canceladas = canceladas;
            this.totalVisitantes = totalVisitantes;
            this.totalVisitantesPorCulto = totalVisitantesPorCulto;
            this.totalAceitouJesus = totalAceitouJesus;
            this.totalReconciliacao = totalReconciliacao;
            this.totalBatismo = totalBatismo;
        }

        public long getTotal() { return total; }
        public long getEmAndamento() { return emAndamento; }
        public long getConcluidas() { return concluidas; }
        public long getCanceladas() { return canceladas; }
        public long getTotalVisitantes() { return totalVisitantes; }
        public long getTotalVisitantesPorCulto() { return totalVisitantesPorCulto; }
        public long getTotalAceitouJesus() { return totalAceitouJesus; }
        public long getTotalReconciliacao() { return totalReconciliacao; }
        public long getTotalBatismo() { return totalBatismo; }
    }
}