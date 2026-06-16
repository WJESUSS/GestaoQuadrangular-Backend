package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.RegistroAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface AuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

    // Filtro geral corrigido para a tipagem do PostgreSQL no Hibernate 6
    @Query("""
        SELECT r FROM RegistroAuditoria r
        WHERE (CAST(:entidade AS string) IS NULL OR UPPER(r.entidade) = UPPER(CAST(:entidade AS string)))
          AND (CAST(:acao AS string)     IS NULL OR UPPER(r.acao)     = UPPER(CAST(:acao AS string)))
          AND (CAST(:usuario AS string)  IS NULL OR LOWER(r.usuarioNome) LIKE LOWER(CONCAT('%', CAST(:usuario AS string), '%')))
          AND (COALESCE(:entidadeId, null) IS NULL OR r.entidadeId = :entidadeId)
          AND (COALESCE(:de, null)         IS NULL OR r.dataHora   >= :de)
          AND (COALESCE(:ate, null)        IS NULL OR r.dataHora   <= :ate)
    """)
// No AuditoriaRepository — troque LocalDateTime por OffsetDateTime nos params de data:
    Page<RegistroAuditoria> filtrar(
            String entidade, String acao, String usuario,
            Long entidadeId,
            OffsetDateTime de,      // ← era LocalDateTime
            OffsetDateTime ate,     // ← era LocalDateTime
            Pageable pageable
    );

    // Histórico de um registro específico
    Page<RegistroAuditoria> findByEntidadeAndEntidadeIdOrderByDataHoraDesc(
            String entity, Long entidadeId, Pageable pageable
    );
}