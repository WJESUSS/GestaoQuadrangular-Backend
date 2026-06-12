package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.Presenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresencaRepository extends JpaRepository<Presenca, Long> {

    /**
     * JOIN FETCH evita N+1: sem ele o Hibernate faria uma query extra
     * pra cada Presenca para carregar o Membro.
     */
    @Query("SELECT p FROM Presenca p JOIN FETCH p.membro WHERE p.relatorio.id = :relatorioId")
    List<Presenca> findByRelatorioId(@Param("relatorioId") Long relatorioId);

    @Query("SELECT p FROM Presenca p JOIN FETCH p.membro WHERE p.relatorio.id = :relatorioId AND p.presente = false")
    List<Presenca> findByRelatorioIdAndPresenteFalse(@Param("relatorioId") Long relatorioId);

    /**
     * Delete direto no banco — evita carregar todos os registros em memória
     * só para depois deletar (o padrão findAll + deleteAll faz isso).
     * Use no lugar de: presencaRepository.deleteAll(presencaRepository.findByRelatorioId(id))
     */
    @Modifying
    @Query("DELETE FROM Presenca p WHERE p.relatorio.id = :relatorioId")
    void deleteByRelatorioId(@Param("relatorioId") Long relatorioId);
}