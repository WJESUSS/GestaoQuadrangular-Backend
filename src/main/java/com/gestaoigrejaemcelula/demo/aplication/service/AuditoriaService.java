package com.gestaoigrejaemcelula.demo.aplication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestaoigrejaemcelula.demo.aplication.dto.AuditoriaDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.RegistroAuditoria;
import com.gestaoigrejaemcelula.demo.domain.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);
    private static final ZoneOffset OFFSET_SP = ZoneOffset.of("-03:00");

    private final AuditoriaRepository repo;
    private final ObjectMapper mapper;

    // ── Registrar evento ─────────────────────────────────────────────────────
    public void registrar(
            String entidade,
            Long   entidadeId,
            String entidadeNome,
            String acao,
            Map<String, Object> detalhes,
            String usuarioNome,
            String usuarioEmail,
            String aprovadorNome,
            String aprovadorEmail,
            String ipOrigem
    ) {
        try {
            String detalhesJson = detalhes != null ? mapper.writeValueAsString(detalhes) : null;
            RegistroAuditoria reg = RegistroAuditoria.builder()
                    .entidade(entidade)
                    .entidadeId(entidadeId)
                    .entidadeNome(entidadeNome)
                    .acao(acao)
                    .detalhes(detalhesJson)
                    .usuarioNome(usuarioNome)
                    .usuarioEmail(usuarioEmail)
                    .aprovadorNome(aprovadorNome)
                    .aprovadorEmail(aprovadorEmail)
                    .dataHora(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .ipOrigem(ipOrigem)
                    .build();
            repo.save(reg);
        } catch (Exception e) {
            log.error("Erro ao registrar auditoria", e);
        }
    }

    // ── Listar com filtros e paginação ───────────────────────────────────────
    public Page<AuditoriaDTO> listar(
            String entidade, String acao, String usuario,
            Long entidadeId, LocalDateTime de, LocalDateTime ate,
            int page, int size
    ) {
        // ✅ Converte para OffsetDateTime em SP antes de comparar com o banco
        OffsetDateTime deOffset  = de  != null ? de.atOffset(OFFSET_SP)  : null;
        OffsetDateTime ateOffset = ate != null ? ate.atOffset(OFFSET_SP) : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataHora").descending());
        return repo.filtrar(entidade, acao, usuario, entidadeId, deOffset, ateOffset, pageable)
                .map(this::toDTO);
    }

    // ── Histórico de um registro específico ──────────────────────────────────
    public Page<AuditoriaDTO> historicoPorRegistro(
            String entidade, Long entidadeId, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataHora").descending());
        return repo.findByEntidadeAndEntidadeIdOrderByDataHoraDesc(entidade, entidadeId, pageable)
                .map(this::toDTO);
    }

    // ── Mapper ───────────────────────────────────────────────────────────────
    private AuditoriaDTO toDTO(RegistroAuditoria r) {
        return AuditoriaDTO.builder()
                .id(r.getId())
                .entidade(r.getEntidade())
                .entidadeId(r.getEntidadeId())
                .entidadeNome(r.getEntidadeNome())
                .acao(r.getAcao())
                .detalhes(r.getDetalhes())
                .usuarioNome(r.getUsuarioNome())
                .usuarioEmail(r.getUsuarioEmail())
                .aprovadorNome(r.getAprovadorNome())
                .aprovadorEmail(r.getAprovadorEmail())
                .dataHora(r.getDataHora()
                        .withOffsetSameInstant(OFFSET_SP)
                        .toLocalDateTime())
                .ipOrigem(r.getIpOrigem())
                .build();
    }
}