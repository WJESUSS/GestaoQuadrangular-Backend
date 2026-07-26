package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.entity.Notificacao;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.NotificacaoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final WhatsAppService whatsAppService;

    @Value("${whatsapp.api.template.notificacao:notificacao_geral}")
    private String templateNotificacao;

    @Value("${whatsapp.api.template.parabens:parabens_relatorio_mensal}")
    private String templateParabens;

    @Transactional
    public void enviarNotificacao(Long usuarioId, String titulo, String mensagem, Notificacao.TipoNotificacao tipo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usuarioId));

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setDataEnvio(LocalDateTime.now());
        notificacao.setLida(false);

        notificacaoRepository.save(notificacao);

        enviarWhatsAppSePossivel(usuario, titulo);
    }

    @Transactional
    public void enviarNotificacaoParaVarios(List<Long> usuarioIds, String titulo, String mensagem, Notificacao.TipoNotificacao tipo) {
        List<Usuario> usuarios = usuarioRepository.findAllById(usuarioIds);
        List<Notificacao> notificacoes = new java.util.ArrayList<>();
        LocalDateTime agora = LocalDateTime.now();
        for (Usuario usuario : usuarios) {
            Notificacao notificacao = new Notificacao();
            notificacao.setUsuario(usuario);
            notificacao.setTitulo(titulo);
            notificacao.setMensagem(mensagem);
            notificacao.setTipo(tipo);
            notificacao.setDataEnvio(agora);
            notificacao.setLida(false);
            notificacoes.add(notificacao);

            enviarWhatsAppSePossivel(usuario, titulo);
        }
        notificacaoRepository.saveAll(notificacoes);
    }

    private void enviarWhatsAppSePossivel(Usuario usuario, String titulo) {
        enviarWhatsApp(usuario, titulo, templateNotificacao);
    }

    private void enviarWhatsApp(Usuario usuario, String titulo, String template) {
        if (usuario.getTelefoneWhatsapp() == null || usuario.getTelefoneWhatsapp().isBlank()) {
            return;
        }
        whatsAppService.enviarTemplate(
                usuario.getTelefoneWhatsapp(),
                template,
                "pt_BR",
                usuario.getNome().split(" ")[0],
                titulo
        );
    }

    public void enviarWhatsAppParabens(Usuario usuario) {
        if (usuario.getTelefoneWhatsapp() == null || usuario.getTelefoneWhatsapp().isBlank()) {
            return;
        }
        whatsAppService.enviarTemplate(
                usuario.getTelefoneWhatsapp(),
                templateParabens,
                "pt_BR",
                usuario.getNome().split(" ")[0]
        );
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