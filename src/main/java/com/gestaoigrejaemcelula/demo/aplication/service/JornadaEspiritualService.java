package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.EtapaEspiritualDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RegistrarDecisaoRequest;
import com.gestaoigrejaemcelula.demo.domain.entity.JornadaEspiritual;
import com.gestaoigrejaemcelula.demo.domain.entity.Meta;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.repository.JornadaEspiritualRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MetaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.VisitanteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class JornadaEspiritualService {

    private final JornadaEspiritualRepository jornadaRepository;
    private final VisitanteRepository visitanteRepository;
    private final MetaRepository metaRepository;
    private final AuditoriaHelper auditoria;

    public JornadaEspiritualService(JornadaEspiritualRepository jornadaRepository,
                                    VisitanteRepository visitanteRepository,
                                    MetaRepository metaRepository,
                                    AuditoriaHelper auditoria) {
        this.jornadaRepository   = jornadaRepository;
        this.visitanteRepository = visitanteRepository;
        this.metaRepository      = metaRepository;
        this.auditoria           = auditoria;
    }

    @Transactional
    public List<EtapaEspiritualDTO> registrarDecisao(Long visitanteId, RegistrarDecisaoRequest req) {

        if (req.getDecisao() == DecisaoEspiritual.NENHUMA) {
            throw new IllegalArgumentException("'NENHUMA' não pode ser registrada como etapa da jornada.");
        }

        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado: " + visitanteId));

        if (jornadaRepository.existsByVisitanteIdAndDecisao(visitanteId, req.getDecisao())) {
            throw new IllegalStateException(
                    "Esta decisão já foi registrada para este visitante: " + req.getDecisao());
        }

        String usuarioLogado = usuarioAtual();

        JornadaEspiritual etapa = new JornadaEspiritual();
        etapa.setVisitante(visitante);
        etapa.setDecisao(req.getDecisao());
        etapa.setDataRegistro(req.getDataRegistro() != null ? req.getDataRegistro() : LocalDate.now());
        etapa.setRegistradoPor(usuarioLogado);
        etapa.setObservacao(req.getObservacao());
        jornadaRepository.save(etapa);

        // Incrementa meta correspondente
        if (visitante.getCelula() != null) {
            Long celulaId   = visitante.getCelula().getId();
            String tipoMeta = decisaoParaTipoMeta(req.getDecisao());

            if (tipoMeta != null) {
                LocalDate mes = etapa.getDataRegistro().withDayOfMonth(1);

                // findByCelulaIdAndTipoMetaAndMesAno retorna Optional<Meta>
                List<Meta> metas = metaRepository
                        .findByCelulaIdAndTipoMetaAndMesAno(celulaId, tipoMeta, mes)
                        .map(List::of)
                        .orElseGet(() ->
                                metaRepository.findByCelulaIdAndTipoMeta(celulaId, tipoMeta)
                                        .stream()
                                        .filter(Meta::isAtiva)
                                        .toList()
                        );

                for (Meta meta : metas) {
                    meta.setMetaAlcancada(meta.getMetaAlcancada() + 1);
                }
                metaRepository.saveAll(metas);
            }
        }

        auditoria.registrar("JORNADA_ESPIRITUAL", visitanteId, visitante.getNome(),
                "DECISAO_REGISTRADA",
                Map.of(
                        "decisao",       Map.of("para", req.getDecisao().name()),
                        "dataRegistro",  Map.of("para", etapa.getDataRegistro().toString()),
                        "registradoPor", Map.of("para", usuarioLogado)
                )
        );

        return listarJornada(visitanteId);
    }

    @Transactional(readOnly = true)
    public List<EtapaEspiritualDTO> listarJornada(Long visitanteId) {
        return jornadaRepository
                .findByVisitanteIdOrderByDataRegistroAsc(visitanteId)
                .stream()
                .map(EtapaEspiritualDTO::new)
                .toList();
    }

    @Transactional
    public void removerDecisao(Long visitanteId, DecisaoEspiritual decisao) {

        JornadaEspiritual etapa = jornadaRepository
                .findByVisitanteIdAndDecisao(visitanteId, decisao)
                .orElseThrow(() -> new RuntimeException(
                        "Etapa '" + decisao + "' não encontrada para o visitante " + visitanteId));

        Visitante visitante = etapa.getVisitante();

        if (visitante.getCelula() != null) {
            Long celulaId   = visitante.getCelula().getId();
            String tipoMeta = decisaoParaTipoMeta(decisao);

            if (tipoMeta != null) {
                LocalDate mes = etapa.getDataRegistro().withDayOfMonth(1);

                // findByCelulaIdAndTipoMetaAndMesAno retorna Optional<Meta>
                List<Meta> metas = metaRepository
                        .findByCelulaIdAndTipoMetaAndMesAno(celulaId, tipoMeta, mes)
                        .map(List::of)
                        .orElseGet(() ->
                                metaRepository.findByCelulaIdAndTipoMeta(celulaId, tipoMeta)
                                        .stream()
                                        .filter(Meta::isAtiva)
                                        .toList()
                        );

                for (Meta meta : metas) {
                    meta.decrementarProgresso();
                }
                metaRepository.saveAll(metas);
            }
        }

        jornadaRepository.delete(etapa);

        auditoria.registrar("JORNADA_ESPIRITUAL", visitanteId, visitante.getNome(),
                "DECISAO_REMOVIDA",
                Map.of("decisao", Map.of("de", decisao.name(), "para", "REMOVIDA"))
        );
    }

    private String decisaoParaTipoMeta(DecisaoEspiritual decisao) {
        return switch (decisao) {
            case ACEITOU_JESUS -> "CONVERSAO";
            case BATISMO_AGUAS -> "BATISMO";
            case RECONCILIOU   -> "RECONCILIACAO";
            default            -> null;
        };
    }

    private String usuarioAtual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "sistema";
    }
}