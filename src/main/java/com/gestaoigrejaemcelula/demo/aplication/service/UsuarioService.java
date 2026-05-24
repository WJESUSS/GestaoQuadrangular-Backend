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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CelulaRepository celulaRepository;
    private final FichaEncontroRepository fichaEncontroRepository;
    private final NotificacaoService notificacaoService;

    // 1️⃣ Cadastrar usuário
    @Transactional
    public Usuario cadastrar(CadastroUsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(dto.getPerfil());
        usuario.setAtivo(dto.isAtivo());

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + dto.getCelulaId()));
            usuario.setCelula(celula);
        }

        return usuarioRepository.save(usuario);
    }

    // 2️⃣ Listar todos usuários
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 3️⃣ Buscar usuário por ID
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    // 4️⃣ Atualizar usuário
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        // 1. Busca o usuário por ID ou lança erro se não existir
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        // 2. Atualiza os campos básicos
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setPerfil(dto.perfil());

        // Se houver lógica de 'ativo' no seu RequestDTO, aplique aqui.
        // Caso contrário, ele mantém o estado atual.

        // 3. Atualiza a senha apenas se uma nova for fornecida
        if (dto.senha() != null && !dto.senha().trim().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        // 4. Trata a associação com a Célula
        if (dto.celulaId() != null) {
            Celula celula = celulaRepository.findById(dto.celulaId())
                    .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + dto.celulaId()));
            usuario.setCelula(celula);
        } else {
            usuario.setCelula(null);
        }

        // 5. Salva as alterações
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // 6. Retorna o ResponseDTO (usando o construtor que você criou)
        return new UsuarioResponseDTO(usuarioSalvo);
    }
    // 5️⃣ Deletar usuário
    @Transactional
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    // 6️⃣ Ativar usuário
    @Transactional
    public void ativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    // 7️⃣ Desativar usuário
    @Transactional
    public void desativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    // 8️⃣ Alternar status (ativo/inativo)
    @Transactional
    public void alternarStatus(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(!usuario.isAtivo());
        usuarioRepository.save(usuario);
    }

    // 9️⃣ Obter usuário logado (baseado no token JWT)
    @Transactional(readOnly = true)
    public Usuario getUsuarioLogado() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
    @Transactional(readOnly = true)
    public List<FichaEncontroResponseDTO> findByUsuarioLogado(String username) {
        // username aqui é o email (padrão Spring Security)
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        List<FichaEncontro> fichas = fichaEncontroRepository
                .findByUsuarioIdOrderByDataCriacaoDesc(usuario.getId());

        return fichas.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    /**
     * Converte FichaEncontro → FichaEncontroResponseDTO (manual, sem MapStruct)
     */
    private FichaEncontroResponseDTO toResponseDTO(FichaEncontro entity) {
        if (entity == null) {
            return null;
        }

        FichaEncontroResponseDTO dto = new FichaEncontroResponseDTO();

        // ID e auditoria
        dto.setId(entity.getId());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setDataAtualizacao(entity.getDataAtualizacao());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : "PENDENTE");
        dto.setCriadoPor(entity.getCriadoPor());
        dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null);

        // Dados pessoais
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

        // Saúde
        dto.setTomaMedicamento(entity.isTomaMedicamento());
        dto.setQualMedicamento(entity.getQualMedicamento());
        dto.setTemProblemasSaude(entity.isTemProblemasSaude());
        dto.setQualProblemaSaude(entity.getQualProblemaSaude());
        dto.setTemApneia(entity.isTemApneia());

        // Contatos e líderes
        dto.setNomeConvidador(entity.getNomeConvidador());
        dto.setCelulaConvidador(entity.getCelulaConvidador());
        dto.setNomeLiderCelula(entity.getNomeLiderCelula());
        dto.setNomeFamiliarContato(entity.getNomeFamiliarContato());
        dto.setTelefoneFamiliarContato(entity.getTelefoneFamiliarContato());

        // Participação e célula
        dto.setFrequentaCelula(entity.isFrequentaCelula());
        dto.setNomeCelula(entity.getNomeCelula());
        dto.setOutrosParticipantes(entity.getOutrosParticipantes());

        // Decisões espirituais
        dto.setAceitouJesus(entity.isAceitouJesus());
        dto.setJaEraCristao(entity.isJaEraCristao());

        // Dados do encontro
        dto.setNomeEncontro(entity.getNomeEncontro());
        dto.setLocalEncontro(entity.getLocalEncontro());
        dto.setTipoEncontro(entity.getTipoEncontro());
        dto.setDataInicio(entity.getDataInicio());
        dto.setDataFim(entity.getDataFim());

        return dto;
    }
    @Transactional
    public SolicitacaoCadastroResponseDTO solicitarCadastroLider(SolicitacaoCadastroLiderDTO dto) {

        // Verifica e-mail duplicado
        if (usuarioRepository.findByEmailIgnoreCase(dto.getEmail().trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Já existe um cadastro com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(dto.getEmail().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(Perfil.LIDER_CELULA);  // sempre LIDER_CELULA
        usuario.setAtivo(false);                  // PENDENTE — admin precisa ativar

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Célula não encontrada com ID: " + dto.getCelulaId()));
            usuario.setCelula(celula);
        }

        Usuario salvo = usuarioRepository.save(usuario);

        return new SolicitacaoCadastroResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getPerfil().name(),
                salvo.isAtivo(),
                "Solicitação recebida! Aguarde a aprovação do administrador para acessar o sistema."
        );
    }

    /**
     * Lista todos os usuários PENDENTES de aprovação (ativo = false).
     * Endpoint exclusivo do admin para ver quem está aguardando.
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPendentes() {
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> !u.isAtivo())
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void rejeitarAlteracao(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);

        boolean temAlteracao = usuario.getEmailPendente() != null || usuario.getSenhaPendente() != null;
        if (!temAlteracao) {
            throw new IllegalArgumentException("Este usuário não possui alteração pendente.");
        }

        usuario.setEmailPendente(null);
        usuario.setSenhaPendente(null);
        usuarioRepository.save(usuario);

        // Notifica o líder
        notificacaoService.enviarNotificacao(
                usuario.getId(),
                "Solicitação rejeitada",
                "Sua solicitação de alteração de dados foi rejeitada pelo administrador. Em caso de dúvidas, entre em contato.",
                com.gestaoigrejaemcelula.demo.domain.entity.Notificacao.TipoNotificacao.REJEICAO_SOLICITACAO
        );
    }

    /**
     * Lista usuários com alterações pendentes (emailPendente ou senhaPendente preenchidos).
     * Usado no painel do admin.
     *
     * GET /usuarios/com-alteracao-pendente   (autenticado como ADMIN)
     */
    @Transactional(readOnly = true)
    public java.util.List<UsuarioResponseDTO> listarComAlteracaoPendente() {
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> u.getEmailPendente() != null || u.getSenhaPendente() != null)
                .map(UsuarioResponseDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void aprovarAlteracao(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);

        boolean temAlteracao = usuario.getEmailPendente() != null || usuario.getSenhaPendente() != null;
        if (!temAlteracao) {
            throw new IllegalArgumentException("Este usuário não possui alteração pendente.");
        }

        // Promove e-mail pendente → e-mail real
        if (usuario.getEmailPendente() != null) {
            usuario.setEmail(usuario.getEmailPendente());
            usuario.setEmailPendente(null);
        }

        // Promove senha pendente → senha real
        if (usuario.getSenhaPendente() != null) {
            usuario.setSenha(usuario.getSenhaPendente());
            usuario.setSenhaPendente(null);
        }

        usuarioRepository.save(usuario);

        // Notifica o líder
        notificacaoService.enviarNotificacao(
                usuario.getId(),
                "Solicitação aprovada",
                "Sua solicitação de alteração de dados foi aprovada pelo administrador.",
                Notificacao.TipoNotificacao.APROVACAO_SOLICITACAO
        );
    }
    @Transactional
    public SolicitacaoAlteracaoResponseDTO solicitarAlteracao(@Valid SolicitacaoAlteracaoDTO dto) {

        // 1. Identifica o usuário pelo e-mail informado (não pelo token)
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(dto.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + dto.getEmail()));

        // 2. Confirma identidade com a senha atual
        if (dto.getSenhaAtual() == null || dto.getSenhaAtual().isBlank()) {
            throw new IllegalArgumentException("A senha atual é obrigatória.");
        }
        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }

        // 3. Valida que ao menos um campo foi enviado
        boolean trocaEmail = dto.getEmailNovo() != null && !dto.getEmailNovo().isBlank();
        boolean trocaSenha = dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank();

        if (!trocaEmail && !trocaSenha) {
            throw new IllegalArgumentException("Informe um novo e-mail e/ou uma nova senha.");
        }

        // 4. Processa troca de e-mail
        if (trocaEmail) {
            String emailNovo = dto.getEmailNovo().trim().toLowerCase();

            if (emailNovo.equalsIgnoreCase(usuario.getEmail())) {
                throw new IllegalArgumentException("O novo e-mail não pode ser igual ao e-mail atual.");
            }
            if (usuarioRepository.findByEmailIgnoreCase(emailNovo).isPresent()) {
                throw new IllegalArgumentException("Este e-mail já está em uso por outro usuário.");
            }

            usuario.setEmailPendente(emailNovo);
        }

        // 5. Processa troca de senha
        if (trocaSenha) {
            if (dto.getConfirmarNovaSenha() == null || dto.getConfirmarNovaSenha().isBlank()) {
                throw new IllegalArgumentException("A confirmação da nova senha é obrigatória.");
            }
            if (!dto.getNovaSenha().equals(dto.getConfirmarNovaSenha())) {
                throw new IllegalArgumentException("A nova senha e a confirmação não coincidem.");
            }
            if (passwordEncoder.matches(dto.getNovaSenha(), usuario.getSenha())) {
                throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual.");
            }

            usuario.setSenhaPendente(passwordEncoder.encode(dto.getNovaSenha()));
        }

        // 6. Salva pendente
        usuarioRepository.save(usuario);

        // 7. Notifica
        notificacaoService.enviarNotificacao(
                usuario.getId(),

                "Solicitação de alteração de dados",
                "O usuário " + usuario.getNome() + " solicitou alteração de " +
                        (trocaEmail && trocaSenha ? "e-mail e senha" : trocaEmail ? "e-mail" : "senha") +
                        ". Acesse o painel para aprovar ou rejeitar.",
                Notificacao.TipoNotificacao.SOLICITACAO_ALTERACAO
        );

        // 8. Retorna resposta
        return new SolicitacaoAlteracaoResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                trocaEmail  ? usuario.getEmailPendente() : null,
                trocaSenha,
                "Solicitação recebida! Aguarde a aprovação do administrador."
        );
    }
    public void atualizarFoto(Long id, String fotoBase64) {
        Usuario usuario = buscarPorId(id);
        usuario.setFotoPerfil(fotoBase64);
        usuarioRepository.save(usuario);
    }
}