package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.*;
import com.gestaoigrejaemcelula.demo.domain.enums.OrigemVisitante;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CelulaService {

    private static final Logger log = LoggerFactory.getLogger(CelulaService.class);

    private final CelulaRepository celulaRepository;
    private final MembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository;
    private final VisitanteRepository visitanteRepository;
    private final NotificacaoService notificacaoService;
    private final AuditoriaHelper auditoria;

    public CelulaService(
            CelulaRepository celulaRepository,
            MembroRepository membroRepository,
            UsuarioRepository usuarioRepository,
            VisitanteRepository visitanteRepository,
            NotificacaoService notificacaoService,
            AuditoriaHelper auditoria
    ) {
        this.celulaRepository    = celulaRepository;
        this.membroRepository    = membroRepository;
        this.usuarioRepository   = usuarioRepository;
        this.visitanteRepository = visitanteRepository;
        this.notificacaoService  = notificacaoService;
        this.auditoria           = auditoria;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private String str(Object o) { return o != null ? o.toString() : ""; }

    // =========================
    // CADASTRAR CÉLULA
    // =========================
    @Transactional
    public CelulaResponseDTO cadastrar(CelulaRequestDTO dto) {
        Usuario lider = usuarioRepository.findById(dto.liderId())
                .orElseThrow(() -> new RuntimeException("Líder não encontrado"));

        Celula celula = new Celula();
        celula.setNome(dto.nome());
        celula.setAnfitriao(dto.anfitriao());
        celula.setEndereco(dto.endereco());
        celula.setBairro(dto.bairro());
        celula.setDiaSemana(dto.diaSemana());
        celula.setHorario(dto.horario());
        celula.setLider(lider);
        celula.setAtiva(true);

        Celula celulaSalva = celulaRepository.saveAndFlush(celula);

        lider.setCelula(celulaSalva);
        usuarioRepository.saveAndFlush(lider);

        auditoria.registrar("CELULA", celulaSalva.getId(), celulaSalva.getNome(), "CREATE",
                Map.of(
                        "lider",    Map.of("para", str(lider.getNome())),
                        "endereco", Map.of("para", str(dto.endereco())),
                        "horario",  Map.of("para", str(dto.horario()))
                )
        );

        return new CelulaResponseDTO(celulaSalva);
    }

    // =========================
    // LISTAR CÉLULAS ATIVAS
    // =========================
    @Transactional(readOnly = true)
    public List<CelulaResponseDTO> listar() {
        return celulaRepository.findByAtivaTrue()
                .stream()
                .map(CelulaResponseDTO::new)
                .toList();
    }

    // =========================
    // BUSCAR POR NOME
    // =========================
    @Transactional(readOnly = true)
    public List<CelulaResponseDTO> buscarPorNome(String nome) {
        return celulaRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(CelulaResponseDTO::new)
                .toList();
    }

    // =========================
    // ATUALIZAR CÉLULA
    // =========================
    @Transactional
    public CelulaResponseDTO atualizar(Long id, CelulaRequestDTO dto) {
        Celula celula = celulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        // Monta diff ANTES de alterar
        Map<String, Object> diff = new LinkedHashMap<>();
        if (!Objects.equals(celula.getNome(), dto.nome()))
            diff.put("nome",      Map.of("de", str(celula.getNome()),      "para", str(dto.nome())));
        if (!Objects.equals(celula.getAnfitriao(), dto.anfitriao()))
            diff.put("anfitriao", Map.of("de", str(celula.getAnfitriao()), "para", str(dto.anfitriao())));
        if (!Objects.equals(celula.getEndereco(), dto.endereco()))
            diff.put("endereco",  Map.of("de", str(celula.getEndereco()),  "para", str(dto.endereco())));
        if (!Objects.equals(celula.getBairro(), dto.bairro()))
            diff.put("bairro",    Map.of("de", str(celula.getBairro()),    "para", str(dto.bairro())));
        if (!Objects.equals(celula.getDiaSemana(), dto.diaSemana()))
            diff.put("diaSemana", Map.of("de", str(celula.getDiaSemana()), "para", str(dto.diaSemana())));
        if (!Objects.equals(celula.getHorario(), dto.horario()))
            diff.put("horario",   Map.of("de", str(celula.getHorario()),   "para", str(dto.horario())));

        celula.setNome(dto.nome());
        celula.setAnfitriao(dto.anfitriao());
        celula.setEndereco(dto.endereco());
        celula.setBairro(dto.bairro());
        celula.setDiaSemana(dto.diaSemana());
        celula.setHorario(dto.horario());

        if (!celula.getLider().getId().equals(dto.liderId())) {
            Usuario novoLider = usuarioRepository.findById(dto.liderId())
                    .orElseThrow(() -> new RuntimeException("Novo líder não encontrado"));

            if (novoLider.getPerfil() != Perfil.LIDER_CELULA) {
                throw new RuntimeException("Usuário não possui perfil de líder");
            }

            diff.put("lider", Map.of("de", str(celula.getLider().getNome()), "para", str(novoLider.getNome())));
            celula.setLider(novoLider);
        }

        CelulaResponseDTO resposta = new CelulaResponseDTO(celulaRepository.save(celula));

        if (!diff.isEmpty())
            auditoria.registrar("CELULA", id, celula.getNome(), "UPDATE", diff);

        return resposta;
    }

    // =========================
    // BUSCAR CÉLULA DO LÍDER
    // =========================
    @Transactional(readOnly = true)
    public Celula buscarCelulaDoLider(Long liderId) {
        return celulaRepository.findByLider_IdAndAtivaTrue(liderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Líder não possui célula ativa"));
    }

    // =========================
    // MEMBROS
    // =========================
    @Transactional
    public void adicionarMembro(Long celulaId, Long membroId) {
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        if (membro.getCelula() != null) {
            throw new RuntimeException("Membro já pertence a uma célula");
        }

        membro.setCelula(celula);
        membroRepository.save(membro);

        auditoria.registrar("MEMBRO", membroId, membro.getNome(), "UPDATE",
                Map.of("celula", Map.of("de", "", "para", str(celula.getNome())))
        );
    }

    @Transactional
    public void removerMembro(Long celulaId, Long membroId) {
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        if (membro.getCelula() == null || !membro.getCelula().getId().equals(celulaId)) {
            throw new RuntimeException("Membro não pertence a esta célula");
        }

        Celula celula = membro.getCelula();
        String nomeCelula = str(celula.getNome());

        membro.setCelula(null);
        membroRepository.save(membro);

        if (celula.getQuantidadeMembrosAtivos() < 8) {
            celula.setStatusMultiplicacao(Celula.StatusMultiplicacao.NORMAL);
            celulaRepository.save(celula);
        }

        auditoria.registrar("MEMBRO", membroId, membro.getNome(), "UPDATE",
                Map.of("celula", Map.of("de", nomeCelula, "para", ""))
        );
    }

    @Transactional
    public void transferirMembro(TransferirMembroDTO dto) {
        Membro membro = membroRepository.findById(dto.getMembroId())
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        if (membro.getStatus() == StatusMembro.FALECIDO ||
                membro.getStatus() == StatusMembro.TRANSFERIDO) {
            throw new RuntimeException("Membro não pode ser transferido");
        }

        Celula novaCelula = celulaRepository.findById(dto.getNovaCelulaId())
                .orElseThrow(() -> new RuntimeException("Nova célula não encontrada"));

        String celulaAnterior = membro.getCelula() != null ? str(membro.getCelula().getNome()) : "";

        membro.setCelula(novaCelula);
        membroRepository.save(membro);

        auditoria.registrar("MEMBRO", membro.getId(), membro.getNome(), "UPDATE",
                Map.of("celula", Map.of("de", celulaAnterior, "para", str(novaCelula.getNome())))
        );
    }

    // =========================
    // VISITANTES
    // =========================
    @Transactional
    public VisitanteResponseDTO salvarVisitanteNaCelula(Long celulaId, VisitanteRequestDTO dto) {
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        Visitante visitante = new Visitante();
        visitante.setNome(dto.getNome());
        visitante.setTelefone(dto.getTelefone());
        visitante.setEmail(dto.getEmail());
        visitante.setDataPrimeiraVisita(LocalDate.now());
        visitante.setOrigem(OrigemVisitante.CELULA);
        visitante.setCelula(celula);

        Visitante salvo = visitanteRepository.save(visitante);

        auditoria.registrar("VISITANTE", salvo.getId(), salvo.getNome(), "CREATE",
                Map.of("celula", Map.of("para", str(celula.getNome())))
        );

        return converterVisitante(salvo);
    }

    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarVisitantesPorCelula(Long celulaId) {
        return visitanteRepository.findByCelulaId(celulaId)
                .stream()
                .map(this::converterVisitante)
                .toList();
    }

    private VisitanteResponseDTO converterVisitante(Visitante v) {
        VisitanteResponseDTO dto = new VisitanteResponseDTO();
        dto.setId(v.getId());
        dto.setNome(v.getNome());
        dto.setTelefone(v.getTelefone());
        dto.setEmail(v.getEmail());
        dto.setDataPrimeiraVisita(v.getDataPrimeiraVisita());
        dto.setOrigem(v.getOrigem());
        return dto;
    }

    @Transactional(readOnly = true)
    public Celula buscarPorId(Long id) {
        return celulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada com id: " + id));
    }

    @Transactional
    public void verificarELancarAlertaMultiplicacao(Long celulaId) {
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + celulaId));

        int qtdMembros = celula.getQuantidadeMembrosAtivos();

        if (qtdMembros >= 8 && celula.getStatusMultiplicacao() == Celula.StatusMultiplicacao.NORMAL) {
            celula.setStatusMultiplicacao(Celula.StatusMultiplicacao.SOLICITADO);
            celula.setDataSolicitacaoMultiplicacao(LocalDateTime.now());
            celulaRepository.save(celula);

            notificacaoService.enviarNotificacao(
                    celula.getLider().getId(),
                    "Sugestão de Multiplicação",
                    "🎉 Parabéns, líder! Sua célula **" + celula.getNome() +
                            "** atingiu **" + qtdMembros + "** membros ativos.\n" +
                            "Chegou a hora de pensar na multiplicação! Clique em 'Solicitar Multiplicação' no seu painel.",
                    Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA
            );
        }
    }

    @Transactional
    public void solicitarMultiplicacao(Long celulaId, String motivo, Long usuarioSolicitanteId) {
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + celulaId));

        if (celula.getStatusMultiplicacao() == Celula.StatusMultiplicacao.EM_ANALISE) {
            throw new IllegalStateException("Esta célula já possui uma solicitação em análise pela secretaria.");
        }

        if (!celula.getLider().getId().equals(usuarioSolicitanteId)) {
            throw new SecurityException("Apenas o líder responsável pela célula pode solicitar a multiplicação.");
        }

        celula.setStatusMultiplicacao(Celula.StatusMultiplicacao.EM_ANALISE);
        celula.setMotivoSolicitacao(motivo != null && !motivo.trim().isEmpty() ? motivo.trim() : "Plano de multiplicação enviado pelo líder.");
        celula.setDataSolicitacaoMultiplicacao(LocalDateTime.now());
        celulaRepository.save(celula);

        auditoria.registrar("CELULA", celulaId, celula.getNome(), "UPDATE",
                Map.of("statusMultiplicacao", Map.of("de", "NORMAL", "para", "EM_ANALISE"),
                        "motivo",              Map.of("para", str(celula.getMotivoSolicitacao())))
        );

        String tituloAdm = "Nova Solicitação de Multiplicação";
        String mensagemAdm = String.format(
                "📢 SOLICITAÇÃO DE MULTIPLICAÇÃO\n\nCélula: %s\nLíder: %s\nMembros Ativos: %d\nMotivo: %s\n\nPor favor, analise a viabilidade no painel administrativo.",
                celula.getNome(), celula.getLider().getNome(),
                celula.getQuantidadeMembrosAtivos(), celula.getMotivoSolicitacao()
        );

        if (celula.getPastor() != null)
            notificacaoService.enviarNotificacao(celula.getPastor().getId(), tituloAdm, mensagemAdm, Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA);

        if (celula.getSecretario() != null)
            notificacaoService.enviarNotificacao(celula.getSecretario().getId(), tituloAdm, mensagemAdm, Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA);

        notificacaoService.enviarNotificacao(
                usuarioSolicitanteId,
                "Solicitação Enviada",
                "Sua solicitação para a célula **" + celula.getNome() + "** foi encaminhada para a secretaria. Aguarde o retorno!",
                Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA
        );
    }

    @Transactional(readOnly = true)
    public List<Membro> listarMembrosDaCelula(Long id) {
        Celula celula = celulaRepository.findByIdWithMembros(id)
                .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com o ID: " + id));
        return celula.getMembros();
    }

    @Transactional(readOnly = true)
    public List<Membro> listarMembrosAtivosDaCelula(Long celulaId) {
        Celula celula = buscarPorId(celulaId);
        return celula.getMembros().stream()
                .filter(m -> m.getStatus() == StatusMembro.ATIVO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Celula buscarCelulaDoLiderComMembros(Long liderId) {
        Celula celula = celulaRepository.findByLiderIdWithMembros(liderId)
                .orElseThrow(() -> new RuntimeException("Líder não possui célula ativa"));

        Hibernate.initialize(celula.getMembros());

        log.debug("Membros na memória: {}", celula.getMembros().size());
        celula.getMembros().forEach(m -> log.debug("  -> Membro: {} | Status: {} | ID: {}", m.getNome(), m.getStatus(), m.getId()));

        return celula;
    }

    @Transactional(readOnly = true)
    public List<CelulaResumoDTO> buscarSolicitacoesPendentes() {
        return celulaRepository.findByStatusMultiplicacao(Celula.StatusMultiplicacao.EM_ANALISE)
                .stream()
                .map(CelulaResumoDTO::new)
                .toList();
    }

    @Transactional
    public void vincularMembro(Long celulaId, Long membroId) {
        Celula celula = celulaRepository.findById(celulaId).orElseThrow();
        Membro membro = membroRepository.findById(membroId).orElseThrow();

        membro.setCelula(celula);
        celula.getMembros().add(membro);
        celulaRepository.save(celula);

        auditoria.registrar("MEMBRO", membroId, membro.getNome(), "UPDATE",
                Map.of("celula", Map.of("de", "", "para", str(celula.getNome())))
        );

        this.verificarELancarAlertaMultiplicacao(celulaId);
    }

    @Transactional
    public void decidirMultiplicacao(Long celulaId, boolean aprovado) {
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada"));

        String statusAnterior = str(celula.getStatusMultiplicacao());

        celula.setStatusMultiplicacao(aprovado
                ? Celula.StatusMultiplicacao.APROVADO
                : Celula.StatusMultiplicacao.REJEITADO);

        celulaRepository.save(celula);

        auditoria.registrar("CELULA", celulaId, celula.getNome(),
                aprovado ? "APPROVE" : "REJECT",
                Map.of("statusMultiplicacao", Map.of("de", statusAnterior, "para", str(celula.getStatusMultiplicacao())))
        );

        String titulo = aprovado ? "Multiplicação APROVADA" : "Solicitação Indeferida";
        String msg    = aprovado
                ? "🎉 Parabéns! Sua solicitação de multiplicação para a célula " + celula.getNome() + " foi APROVADA!"
                : "⚠️ Olá líder, sua solicitação de multiplicação para a célula " + celula.getNome() + " foi indeferida no momento.";

        notificacaoService.enviarNotificacao(celula.getLider().getId(), titulo, msg, Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA);
    }

    @Transactional
    public CelulaStatusMultiplicacaoDTO atualizarStatusMultiplicacao(Long id, boolean aprovado) {
        Celula celula = celulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        String statusAnterior = str(celula.getStatusMultiplicacao());

        celula.setStatusMultiplicacao(aprovado
                ? Celula.StatusMultiplicacao.APROVADO
                : Celula.StatusMultiplicacao.REJEITADO);

        celulaRepository.save(celula);

        auditoria.registrar("CELULA", id, celula.getNome(),
                aprovado ? "APPROVE" : "REJECT",
                Map.of("statusMultiplicacao", Map.of("de", statusAnterior, "para", str(celula.getStatusMultiplicacao())))
        );

        return new CelulaStatusMultiplicacaoDTO(celula.getId(), celula.getStatusMultiplicacao().name());
    }

    @Transactional(readOnly = true)
    public List<MembroResponseDTO> buscarMembrosPorCelula(Long celulaId) {
        Celula celula = celulaRepository.findByIdWithMembros(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));
        return celula.getMembros()
                .stream()
                .map(MembroResponseDTO::new)
                .collect(Collectors.toList());
    }
}