package com.gestaoigrejaemcelula.demo.domain.repository;


import com.gestaoigrejaemcelula.demo.domain.entity.Notificacao;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuarioIdAndLidaFalseOrderByDataEnvioDesc(Long usuarioId);

    List<Notificacao> findByUsuarioIdOrderByDataEnvioDesc(Long usuarioId);

    long countByUsuarioIdAndLidaFalse(Long usuarioId);
}

