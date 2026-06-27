package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.FichaEncontro;
import com.gestaoigrejaemcelula.demo.domain.entity.Notificacao;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.FichaEncontroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.NotificacaoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CelulaRepository celulaRepository;
    private final FichaEncontroRepository fichaEncontroRepository;
    private final NotificacaoService notificacaoService;
    private final WhatsAppService whatsAppService;
    private final AuditoriaHelper auditoria;

    @Value("${whatsapp.api.template.notificacao:notificacao_geral}")
    private String templateBoasVindas;

    @Value("${foto.perfil.max-size-bytes:524288}")
    private int maxFotoSizeBytes;

    // ── Helper ─────────────────────────────────────────────────────────────────
    private String str(Object o) { return o != null ? o.toString() : ""; }

    // =========================
    // 1 — CADASTRAR
    // =========================
    @Transactional
    public Usuario cadastrar(CadastroUsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(dto.getPerfil());
        usuario.setAtivo(dto.isAtivo());
        usuario.setTelefoneWhatsapp(dto.getTelefoneWhatsapp());

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + dto.getCelulaId()));
            usuario.setCelula(celula);
        }

        Usuario salvo = usuarioRepository.save(usuario);

        auditoria.registrar("USUARIO", salvo.getId(), salvo.getNome(), "CREATE",
                Map.of(
                        "email",  Map.of("para", str(salvo.getEmail())),
                        "perfil", Map.of("para", str(salvo.getPerfil())),
                        "ativo",  Map.of("para", str(salvo.isAtivo()))
                )
        );

        enviarBoasVindas(salvo);

        return salvo;
    }

    // =========================
    // 2 — LISTAR TODOS
    // =========================
    @Transactional(readOnly = true)
    public List<UsuarioResumoDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResumoDTO::new)
                .collect(Collectors.toList());
    }

    // =========================
    // 3 — BUSCAR POR ID
    // =========================
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    // =========================
    // 4 — ATUALIZAR
    // =========================
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        // Monta diff ANTES de alterar
        Map<String, Object> diff = new LinkedHashMap<>();
        if (!Objects.equals(usuario.getNome(), dto.nome()))
            diff.put("nome",   Map.of("de", str(usuario.getNome()),   "para", str(dto.nome())));
        if (!Objects.equals(usuario.getEmail(), dto.email()))
            diff.put("email",  Map.of("de", str(usuario.getEmail()),  "para", str(dto.email())));
        if (!Objects.equals(usuario.getPerfil(), dto.perfil()))
            diff.put("perfil", Map.of("de", str(usuario.getPerfil()), "para", str(dto.perfil())));
        if (dto.celulaId() != null) {
            Long celulaAtualId = usuario.getCelula() != null ? usuario.getCelula().getId() : null;
            if (!Objects.equals(celulaAtualId, dto.celulaId()))
                diff.put("celulaId", Map.of("de", str(celulaAtualId), "para", str(dto.celulaId())));
        }
        if (dto.senha() != null && !dto.senha().trim().isEmpty())
            diff.put("senha", Map.of("para", "*** alterada ***"));
        if (!Objects.equals(usuario.getTelefoneWhatsapp(), dto.telefoneWhatsapp()))
            diff.put("telefoneWhatsapp", Map.of("de", str(usuario.getTelefoneWhatsapp()), "para", str(dto.telefoneWhatsapp())));

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setPerfil(dto.perfil());
        usuario.setTelefoneWhatsapp(dto.telefoneWhatsapp());

        if (dto.senha() != null && !dto.senha().trim().isEmpty())
            usuario.setSenha(passwordEncoder.encode(dto.senha()));

        if (dto.celulaId() != null) {
            Celula celula = celulaRepository.findById(dto.celulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + dto.celulaId()));
            usuario.setCelula(celula);
        } else {
            usuario.setCelula(null);
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        if (!diff.isEmpty())
            auditoria.registrar("USUARIO", id, usuarioSalvo.getNome(), "UPDATE", diff);

        return new UsuarioResponseDTO(usuarioSalvo);
    }

    // =========================
    // 5 — DELETAR
    // =========================
    @Transactional
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
        auditoria.registrar("USUARIO", id, usuario.getNome(), "DELETE", null);
    }

    // =========================
    // 6 — ATIVAR
    // =========================
    @Transactional
    public void ativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
        auditoria.registrar("USUARIO", id, usuario.getNome(), "UPDATE",
                Map.of("ativo", Map.of("de", "false", "para", "true"))
        );
    }

    // =========================
    // 7 — DESATIVAR
    // =========================
    @Transactional
    public void desativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        auditoria.registrar("USUARIO", id, usuario.getNome(), "UPDATE",
                Map.of("ativo", Map.of("de", "true", "para", "false"))
        );
    }

    // =========================
    // 8 — ALTERNAR STATUS
    // =========================
    @Transactional
    public void alternarStatus(Long id) {
        Usuario usuario = buscarPorId(id);
        boolean anterior = usuario.isAtivo();
        usuario.setAtivo(!anterior);
        usuarioRepository.save(usuario);
        auditoria.registrar("USUARIO", id, usuario.getNome(), "UPDATE",
                Map.of("ativo", Map.of("de", str(anterior), "para", str(!anterior)))
        );
    }

    // =========================
    // 9 — USUÁRIO LOGADO
    // =========================
    @Transactional(readOnly = true)
    public Usuario getUsuarioLogado() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
            throw new AccessDeniedException("Usuário não autenticado");

        String email = authentication.getName();
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    // =========================
    // FICHAS DO USUÁRIO LOGADO
    // =========================
    @Transactional(readOnly = true)
    public List<FichaEncontroResponseDTO> findByUsuarioLogado(String username) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        List<FichaEncontro> fichas = fichaEncontroRepository
                .findByUsuarioIdOrderByDataCriacaoDesc(usuario.getId());

        return fichas.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private FichaEncontroResponseDTO toResponseDTO(FichaEncontro entity) {
        if (entity == null) return null;

        FichaEncontroResponseDTO dto = new FichaEncontroResponseDTO();
        dto.setId(entity.getId());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setDataAtualizacao(entity.getDataAtualizacao());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : "PENDENTE");
        dto.setCriadoPor(entity.getCriadoPor());
        dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null);
        dto.setNome(entity.getNomeConvidado());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setEndereco(entity.getEndereco());
        dto.setBairro(entity.getBairro());
        dto.setCidade(entity.getCidade());
        dto.setTelefone(entity.getTelefone());
        dto.setSexo(entity.getSexo() != null ? entity.getSexo().name() : null);
        dto.setEstadoCivil(entity.getEstadoCivil() != null ? entity.getEstadoCivil().name() : null);
        dto.setRg(entity.getRg());
        dto.setEstado(entity.getEstado());
        dto.setPeso(entity.getPeso());
        dto.setAltura(entity.getAltura());
        dto.setTomaMedicamento(entity.isTomaMedicamento());
        dto.setQualMedicamento(entity.getQualMedicamento());
        dto.setTemProblemasSaude(entity.isTemProblemasSaude());
        dto.setQualProblemaSaude(entity.getQualProblemaSaude());
        dto.setTemApneia(entity.isTemApneia());
        dto.setNomeConvidador(entity.getNomeConvidador());
        dto.setCelulaConvidador(entity.getCelulaConvidador());
        dto.setNomeLiderCelula(entity.getNomeLiderCelula());
        dto.setNomeFamiliarContato(entity.getNomeFamiliarContato());
        dto.setTelefoneFamiliarContato(entity.getTelefoneFamiliarContato());
        dto.setFrequentaCelula(entity.isFrequentaCelula());
        dto.setNomeCelula(entity.getNomeCelula());
        dto.setOutrosParticipantes(entity.getOutrosParticipantes());
        dto.setAceitouJesus(entity.isAceitouJesus());
        dto.setJaEraCristao(entity.isJaEraCristao());
        dto.setNomeEncontro(entity.getNomeEncontro());
        dto.setLocalEncontro(entity.getLocalEncontro());
        dto.setTipoEncontro(entity.getTipoEncontro());
        dto.setDataInicio(entity.getDataInicio());
        dto.setDataFim(entity.getDataFim());
        return dto;
    }

    // =========================
    // SOLICITAR CADASTRO LÍDER
    // =========================
    @Transactional
    public SolicitacaoCadastroResponseDTO solicitarCadastroLider(SolicitacaoCadastroLiderDTO dto) {
        if (usuarioRepository.findByEmailIgnoreCase(dto.getEmail().trim().toLowerCase()).isPresent())
            throw new IllegalArgumentException("Já existe um cadastro com este e-mail.");

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(dto.getEmail().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(Perfil.LIDER_CELULA);
        usuario.setAtivo(false);
        usuario.setTelefoneWhatsapp(dto.getTelefoneWhatsapp());

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + dto.getCelulaId()));
            usuario.setCelula(celula);
        }

        Usuario salvo = usuarioRepository.save(usuario);

        auditoria.registrar("USUARIO", salvo.getId(), salvo.getNome(), "CREATE",
                Map.of(
                        "perfil", Map.of("para", "LIDER_CELULA"),
                        "ativo",  Map.of("para", "false (pendente aprovação)")
                )
        );

        return new SolicitacaoCadastroResponseDTO(
                salvo.getId(), salvo.getNome(), salvo.getEmail(),
                salvo.getPerfil().name(), salvo.isAtivo(),
                "Solicitação recebida! Aguarde a aprovação do administrador para acessar o sistema."
        );
    }

    // =========================
    // LISTAR PENDENTES
    // =========================
    @Transactional(readOnly = true)
    public List<UsuarioResumoDTO> listarPendentes() {
        return usuarioRepository.findPendentes().stream()
                .map(UsuarioResumoDTO::new)
                .collect(Collectors.toList());
    }

    // =========================
    // REJEITAR ALTERAÇÃO
    // =========================
    @Transactional
    public void rejeitarAlteracao(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);

        if (usuario.getEmailPendente() == null && usuario.getSenhaPendente() == null)
            throw new IllegalArgumentException("Este usuário não possui alteração pendente.");

        usuario.setEmailPendente(null);
        usuario.setSenhaPendente(null);
        usuarioRepository.save(usuario);

        auditoria.registrarComAprovador("USUARIO", usuarioId, usuario.getNome(),
                "REJECT", str(getEmailLogado()), str(getEmailLogado())
        );

        notificacaoService.enviarNotificacao(
                usuario.getId(),
                "Solicitação rejeitada",
                "Sua solicitação de alteração de dados foi rejeitada pelo administrador. Em caso de dúvidas, entre em contato.",
                Notificacao.TipoNotificacao.REJEICAO_SOLICITACAO
        );
    }

    // =========================
    // LISTAR COM ALTERAÇÃO PENDENTE
    // =========================
    @Transactional(readOnly = true)
    public List<UsuarioResumoDTO> listarComAlteracaoPendente() {
        return usuarioRepository.findComAlteracaoPendente().stream()
                .map(UsuarioResumoDTO::new)
                .collect(Collectors.toList());
    }

    // =========================
    // APROVAR ALTERAÇÃO
    // =========================
    @Transactional
    public void aprovarAlteracao(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);

        if (usuario.getEmailPendente() == null && usuario.getSenhaPendente() == null)
            throw new IllegalArgumentException("Este usuário não possui alteração pendente.");

        Map<String, Object> diff = new LinkedHashMap<>();

        if (usuario.getEmailPendente() != null) {
            diff.put("email", Map.of("de", str(usuario.getEmail()), "para", str(usuario.getEmailPendente())));
            usuario.setEmail(usuario.getEmailPendente());
            usuario.setEmailPendente(null);
        }

        if (usuario.getSenhaPendente() != null) {
            diff.put("senha", Map.of("para", "*** alterada ***"));
            usuario.setSenha(usuario.getSenhaPendente());
            usuario.setSenhaPendente(null);
        }

        usuarioRepository.save(usuario);

        auditoria.registrarComAprovador("USUARIO", usuarioId, usuario.getNome(),
                "APPROVE", str(getEmailLogado()), str(getEmailLogado())
        );

        notificacaoService.enviarNotificacao(
                usuario.getId(),
                "Solicitação aprovada",
                "Sua solicitação de alteração de dados foi aprovada pelo administrador.",
                Notificacao.TipoNotificacao.APROVACAO_SOLICITACAO
        );
    }

    // =========================
    // SOLICITAR ALTERAÇÃO
    // =========================
    @Transactional
    public SolicitacaoAlteracaoResponseDTO solicitarAlteracao(@Valid SolicitacaoAlteracaoDTO dto) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(dto.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + dto.getEmail()));

        if (dto.getSenhaAtual() == null || dto.getSenhaAtual().isBlank())
            throw new IllegalArgumentException("A senha atual é obrigatória.");
        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha()))
            throw new IllegalArgumentException("Senha atual incorreta.");

        boolean trocaEmail = dto.getEmailNovo() != null && !dto.getEmailNovo().isBlank();
        boolean trocaSenha = dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank();

        if (!trocaEmail && !trocaSenha)
            throw new IllegalArgumentException("Informe um novo e-mail e/ou uma nova senha.");

        if (trocaEmail) {
            String emailNovo = dto.getEmailNovo().trim().toLowerCase();
            if (emailNovo.equalsIgnoreCase(usuario.getEmail()))
                throw new IllegalArgumentException("O novo e-mail não pode ser igual ao e-mail atual.");
            if (usuarioRepository.findByEmailIgnoreCase(emailNovo).isPresent())
                throw new IllegalArgumentException("Este e-mail já está em uso por outro usuário.");
            usuario.setEmailPendente(emailNovo);
        }

        if (trocaSenha) {
            if (dto.getConfirmarNovaSenha() == null || dto.getConfirmarNovaSenha().isBlank())
                throw new IllegalArgumentException("A confirmação da nova senha é obrigatória.");
            if (!dto.getNovaSenha().equals(dto.getConfirmarNovaSenha()))
                throw new IllegalArgumentException("A nova senha e a confirmação não coincidem.");
            if (passwordEncoder.matches(dto.getNovaSenha(), usuario.getSenha()))
                throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual.");
            usuario.setSenhaPendente(passwordEncoder.encode(dto.getNovaSenha()));
        }

        usuarioRepository.save(usuario);

        auditoria.registrar("USUARIO", usuario.getId(), usuario.getNome(), "UPDATE",
                Map.of("solicitacao", Map.of("para",
                        trocaEmail && trocaSenha ? "e-mail e senha pendentes" :
                                trocaEmail ? "e-mail pendente" : "senha pendente"))
        );

        notificacaoService.enviarNotificacao(
                usuario.getId(),
                "Solicitação de alteração de dados",
                "O usuário " + usuario.getNome() + " solicitou alteração de " +
                        (trocaEmail && trocaSenha ? "e-mail e senha" : trocaEmail ? "e-mail" : "senha") +
                        ". Acesse o painel para aprovar ou rejeitar.",
                Notificacao.TipoNotificacao.SOLICITACAO_ALTERACAO
        );

        return new SolicitacaoAlteracaoResponseDTO(
                usuario.getId(), usuario.getNome(), usuario.getEmail(),
                trocaEmail ? usuario.getEmailPendente() : null,
                trocaSenha,
                "Solicitação recebida! Aguarde a aprovação do administrador."
        );
    }

    // =========================
    // ATUALIZAR FOTO
    // =========================
    @Transactional
    public void atualizarFoto(Long id, String fotoBase64) throws AccessDeniedException {
        if (fotoBase64 == null || fotoBase64.isBlank())
            throw new IllegalArgumentException("A foto não pode estar vazia.");

        String base64Data = fotoBase64;
        if (base64Data.contains(","))
            base64Data = base64Data.substring(base64Data.indexOf(",") + 1);

        byte[] decoded = java.util.Base64.getDecoder().decode(base64Data);
        if (decoded.length > maxFotoSizeBytes)
            throw new IllegalArgumentException("A foto excede o tamanho máximo de " + (maxFotoSizeBytes / 1024) + "KB.");

        Usuario usuario = buscarPorId(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ADMIN"));
        if (!isAdmin && !usuario.getEmail().equalsIgnoreCase(auth.getName()))
            throw new AccessDeniedException("Apenas o próprio usuário ou um administrador pode alterar a foto.");

        usuario.setFotoPerfil(fotoBase64);
        usuarioRepository.save(usuario);
        auditoria.registrar("USUARIO", id, usuario.getNome(), "UPDATE",
                Map.of("fotoPerfil", Map.of("para", "*** imagem atualizada ***"))
        );
    }

    // =========================
    // LISTAR FOTOS
    // =========================
    @Transactional(readOnly = true)
    public Map<Long, String> listarFotos() {
        return usuarioRepository.findFotos().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (String) row[1]
                ));
    }

    // ── Helper interno para pegar e-mail do usuário logado ──────────────────
    private String getEmailLogado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "sistema";
    }

    private void enviarBoasVindas(Usuario usuario) {
        if (usuario.getTelefoneWhatsapp() == null || usuario.getTelefoneWhatsapp().isBlank()) {
            return;
        }
        String primeiroNome = usuario.getNome().split(" ")[0];
        whatsAppService.enviarTemplate(
                usuario.getTelefoneWhatsapp(),
                templateBoasVindas,
                "pt_BR",
                primeiroNome
        );
    }
}