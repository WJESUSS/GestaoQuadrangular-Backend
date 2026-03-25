package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.entity.Notificacao;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.NotificacaoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Envia uma notificação interna para um usuário específico.
     * A notificação é salva no banco e ficará visível no dashboard/app do usuário.
     */
    @Transactional
    public void enviarNotificacao(Long usuarioId, String mensagem, Notificacao.TipoNotificacao tipo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usuarioId));

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setDataEnvio(LocalDateTime.now());
        notificacao.setLida(false);

        notificacaoRepository.save(notificacao);
    }

    /**
     * Envia notificação para múltiplos usuários (ex: pastor + secretário)
     */
    @Transactional
    public void enviarNotificacaoParaVarios(List<Long> usuarioIds, String mensagem, Notificacao.TipoNotificacao tipo) {
        for (Long id : usuarioIds) {
            enviarNotificacao(id, mensagem, tipo);
        }
    }

    /**
     * Marca uma notificação como lida
     */
    @Transactional
    public void marcarComoLida(Long notificacaoId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada: " + notificacaoId));

        if (!notificacao.isLida()) {
            notificacao.setLida(true);
            notificacao.setDataLida(LocalDateTime.now());
            notificacaoRepository.save(notificacao);
        }
    }

    /**
     * Lista todas as notificações não lidas de um usuário, ordenadas por data (mais recente primeiro)
     */
    @Transactional(readOnly = true)
    public List<Notificacao> getNotificacoesNaoLidas(Long usuarioId) {
        return notificacaoRepository.findByUsuarioIdAndLidaFalseOrderByDataEnvioDesc(usuarioId);
    }

    /**
     * Lista todas as notificações de um usuário (lidas e não lidas)
     */
    @Transactional(readOnly = true)
    public List<Notificacao> getTodasNotificacoes(Long usuarioId) {
        return notificacaoRepository.findByUsuarioIdOrderByDataEnvioDesc(usuarioId);
    }

    /**
     * Conta quantas notificações não lidas o usuário tem (para mostrar badge)
     */
    @Transactional(readOnly = true)
    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

}