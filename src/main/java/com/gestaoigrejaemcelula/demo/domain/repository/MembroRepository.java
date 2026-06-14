package com.gestaoigrejaemcelula.demo.domain.repository;


import com.gestaoigrejaemcelula.demo.aplication.dto.AlertaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MembroResumoDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.MembroSelectDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembroRepository extends JpaRepository<Membro, Long> {

    // Buscar por nome (tela de busca)
    List<Membro> findByNomeContainingIgnoreCase(String nome);
    Optional<Membro> findByNomeIgnoreCase(String nome);
    @Query(value = "SELECT m FROM Membro m LEFT JOIN FETCH m.celula c LEFT JOIN FETCH c.lider",
            countQuery = "SELECT count(m) FROM Membro m")
    Page<Membro> findAllComCelulaELider(Pageable pageable);
    List<Membro> findByCelulaIsNull();

    // Listar membros de uma célula específica
    List<Membro> findByCelulaId(Long celulaId);

    // Buscar membros por status
    List<Membro> findByStatus(StatusMembro status);

    @Query("SELECT COUNT(m) FROM Membro m WHERE m.dataCadastro BETWEEN :inicio AND :fim")
    Long novosMembrosMes(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("""
    SELECT new com.gestaoigrejaemcelula.demo.aplication.dto.MembroSelectDTO(
        m.id, m.nome
    )
    FROM Membro m
    WHERE m.status = 'ATIVO'
    ORDER BY m.nome
""")
    List<MembroSelectDTO> listarParaSelect();

    @Query("""
    SELECT new com.gestaoigrejaemcelula.demo.aplication.dto.MembroSelectDTO(
        m.nome
    )
    FROM Membro m
    WHERE m.status = 'ATIVO'
    ORDER BY m.nome
""")
    List<MembroSelectDTO> listarNomesParaSelect();

    @Query("SELECT new com.gestaoigrejaemcelula.demo.aplication.dto.MembroResumoDTO(m.id, m.nome) FROM Membro m WHERE m.status = 'ATIVO' ORDER BY m.nome")
    List<MembroResumoDTO> listarAtivosOrdenados();

    @Query("""
        SELECT m FROM Membro m
        WHERE EXTRACT(MONTH FROM m.dataNascimento) = :mes
        AND EXTRACT(DAY FROM m.dataNascimento) = :dia
        AND m.status = 'ATIVO'
    """)
    List<Membro> findAniversariantesDoDia(
            @Param("mes") int mes,
            @Param("dia") int dia
    );
    @Query("SELECT new com.gestaoigrejaemcelula.demo.aplication.dto.AlertaDTO(" +
            "m.id, m.nome, m.telefone, m.celula.nome, " +
            "(SELECT count(p) FROM Presenca p WHERE p.membro = m AND p.status = 'FALTA' AND p.data >= :dataLimite)) " +
            "FROM Membro m WHERE m.status = com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro.ATIVO")
    List<AlertaDTO> findAlertasMembros(@Param("dataLimite") LocalDate dataLimite);
// ❌ Remove a query antiga findAniversariantesDaSemana e substitui por:

    @Query("""
    SELECT m FROM Membro m
    WHERE m.status = 'ATIVO'
    AND (
        EXTRACT(MONTH FROM m.dataNascimento) * 100 + EXTRACT(DAY FROM m.dataNascimento)
    ) IN :diasMes
""")
    List<Membro> findAniversariantesPorDiasMes(@Param("diasMes") List<Integer> diasMes);
    // Buscar aniversariantes de UMA CÉLULA específica
    @Query("""
    SELECT m FROM Membro m
    WHERE m.celula.id = :celulaId
    AND EXTRACT(MONTH FROM m.dataNascimento) = :mes
    AND EXTRACT(DAY FROM m.dataNascimento) = :dia
    AND m.status = 'ATIVO'
""")
    List<Membro> findAniversariantesDoDiaPorCelula(
            @Param("celulaId") Long celulaId,
            @Param("mes") int mes,
            @Param("dia") int dia
    );

    // Buscar aniversariantes da semana de UMA CÉLULA
    @Query("""
    SELECT m FROM Membro m
    WHERE m.celula.id = :celulaId
    AND m.status = 'ATIVO'
    AND (
        EXTRACT(MONTH FROM m.dataNascimento) * 100 + EXTRACT(DAY FROM m.dataNascimento)
    ) IN :diasMes
""")
    List<Membro> findAniversariantesSemanaPoeCelula(
            @Param("celulaId") Long celulaId,
            @Param("diasMes") List<Integer> diasMes
    );
}
