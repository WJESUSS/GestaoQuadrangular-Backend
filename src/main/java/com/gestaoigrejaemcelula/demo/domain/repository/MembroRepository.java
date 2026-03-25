package com.gestaoigrejaemcelula.demo.domain.repository;


import com.gestaoigrejaemcelula.demo.aplication.dto.MembroSelectDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
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

    // Listar membros SEM célula (para adicionar em célula)
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

}
