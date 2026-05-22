package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.*;
import com.gestaoigrejaemcelula.demo.domain.enums.OrigemVisitante;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CelulaService {

    private final CelulaRepository celulaRepository;
    private final MembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository;
    private final VisitanteRepository visitanteRepository;
    private final NotificacaoService notificacaoService;

    public CelulaService(
            CelulaRepository celulaRepository,
            MembroRepository membroRepository,
            UsuarioRepository usuarioRepository,
            VisitanteRepository visitanteRepository, NotificacaoService notificacaoService
    ) {
        this.celulaRepository = celulaRepository;
        this.membroRepository = membroRepository;
        this.usuarioRepository = usuarioRepository;
        this.visitanteRepository = visitanteRepository;
        this.notificacaoService = notificacaoService;
    }

    // =========================
    // CADASTRAR CÉLULA
    @Transactional
    public CelulaResponseDTO cadastrar(CelulaRequestDTO dto) {
        // 1. Busca o líder no banco de dados
        Usuario lider = usuarioRepository.findById(dto.liderId())
                .orElseThrow(() -> new RuntimeException("Líder não encontrado"));

        // 2. Cria o objeto Célula com os dados do Front-end
        Celula celula = new Celula();
        celula.setNome(dto.nome());
        celula.setAnfitriao(dto.anfitriao());
        celula.setEndereco(dto.endereco());
        celula.setBairro(dto.bairro());
        celula.setDiaSemana(dto.diaSemana());
        celula.setHorario(dto.horario());
        celula.setLider(lider); // Aqui a Célula sabe quem é o líder
        celula.setAtiva(true);

        // 3. SALVA A CÉLULA PRIMEIRO (O saveAndFlush obriga o banco a criar o ID AGORA)
        Celula celulaSalva = celulaRepository.saveAndFlush(celula);

        // 4. AQUI ESTÁ A CORREÇÃO: Vincula a célula ao usuário e SALVA O USUÁRIO
        lider.setCelula(celulaSalva);
        usuarioRepository.saveAndFlush(lider); // <--- Isso faz o ID aparecer na tabela de usuários

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

            celula.setLider(novoLider);
        }

        return new CelulaResponseDTO(celulaRepository.save(celula));
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
    }

    @Transactional
    public void removerMembro(Long celulaId, Long membroId) {
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        if (membro.getCelula() == null || !membro.getCelula().getId().equals(celulaId)) {
            throw new RuntimeException("Membro não pertence a esta célula");
        }

        Celula celula = membro.getCelula();
        membro.setCelula(null);
        membroRepository.save(membro);

        // --- NOVA LÓGICA DE RESET ---
        // Se após a remoção a célula tiver menos de 8 membros,
        // resetamos o status para permitir um novo ciclo no futuro.
        if (celula.getQuantidadeMembrosAtivos() < 8) {
            celula.setStatusMultiplicacao(Celula.StatusMultiplicacao.NORMAL);
            celulaRepository.save(celula);
        }
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

        membro.setCelula(novaCelula);
        membroRepository.save(membro);
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

        return converterVisitante(visitanteRepository.save(visitante));
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

        // Só dispara se ainda não foi solicitado e já atingiu o limite
        if (qtdMembros >= 8 && celula.getStatusMultiplicacao() == Celula.StatusMultiplicacao.NORMAL) {
            celula.setStatusMultiplicacao(Celula.StatusMultiplicacao.SOLICITADO);
            celula.setDataSolicitacaoMultiplicacao(LocalDateTime.now());
            celulaRepository.save(celula);

            // 1. Criamos o título
            String titulo = "Sugestão de Multiplicação";

            // 2. Preparamos a mensagem (removi o parabéns daqui para não ficar repetitivo com o título)
            String mensagem = "🎉 Parabéns, líder! Sua célula **" + celula.getNome() +
                    "** atingiu **" + qtdMembros + "** membros ativos.\n" +
                    "Chegou a hora de pensar na multiplicação! Clique em 'Solicitar Multiplicação' no seu painel.";

            // 3. Chamada corrigida com os 4 parâmetros
            notificacaoService.enviarNotificacao(
                    celula.getLider().getId(),
                    titulo, // <-- O parâmetro que faltava
                    mensagem,
                    Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA
            );
        }
    }

    /**
     * O líder solicita oficialmente a multiplicação da célula.
     * Muda o status para EM_ANALISE e notifica pastor + secretário (se existirem).
     */
    /**
     * O líder solicita oficialmente a multiplicação da célula.
     * Muda o status para EM_ANALISE e notifica a liderança superior.
     */
    @Transactional
    public void solicitarMultiplicacao(Long celulaId, String motivo, Long usuarioSolicitanteId) {
        // 1. Busca a célula ou lança erro 404
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com ID: " + celulaId));

        // 2. Validação de Status: Bloqueia apenas se já houver um processo pendente
        if (celula.getStatusMultiplicacao() == Celula.StatusMultiplicacao.EM_ANALISE) {
            throw new IllegalStateException("Esta célula já possui uma solicitação em análise pela secretaria.");
        }

        // 3. Segurança: Verifica se quem está tentando é de fato o líder da célula
        if (!celula.getLider().getId().equals(usuarioSolicitanteId)) {
            throw new SecurityException("Apenas o líder responsável pela célula pode solicitar a multiplicação.");
        }

        // 4. Atualização dos Dados da Célula
        celula.setStatusMultiplicacao(Celula.StatusMultiplicacao.EM_ANALISE);
        celula.setMotivoSolicitacao(motivo != null && !motivo.trim().isEmpty() ? motivo.trim() : "Plano de multiplicação enviado pelo líder.");
        celula.setDataSolicitacaoMultiplicacao(LocalDateTime.now()); // Registra o momento do pedido

        celulaRepository.save(celula);

        // 5. Preparação das Notificações
        String tituloAdm = "Nova Solicitação de Multiplicação";
        String mensagemAdm = String.format(
                "📢 SOLICITAÇÃO DE MULTIPLICAÇÃO\n\n" +
                        "Célula: %s\n" +
                        "Líder: %s\n" +
                        "Membros Ativos: %d\n" +
                        "Motivo: %s\n\n" +
                        "Por favor, analise a viabilidade no painel administrativo.",
                celula.getNome(),
                celula.getLider().getNome(),
                celula.getQuantidadeMembrosAtivos(),
                celula.getMotivoSolicitacao()
        );

        // 6. Envio para Pastor (se vinculado)
        if (celula.getPastor() != null) {
            notificacaoService.enviarNotificacao(
                    celula.getPastor().getId(),
                    tituloAdm,
                    mensagemAdm,
                    Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA
            );
        }

        // 7. Envio para Secretário (se vinculado)
        if (celula.getSecretario() != null) {
            notificacaoService.enviarNotificacao(
                    celula.getSecretario().getId(),
                    tituloAdm,
                    mensagemAdm,
                    Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA
            );
        }

        // 8. Confirmação para o Líder (Feedback visual de sucesso)
        notificacaoService.enviarNotificacao(
                usuarioSolicitanteId,
                "Solicitação Enviada",
                "Sua solicitação para a célula **" + celula.getNome() + "** foi encaminhada para a secretaria. Aguarde o retorno!",
                Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA
        );
    }
    // Método 1: lista todos os membros (ativos ou não, dependendo do seu desejo)
    public List<Membro> listarMembrosDaCelula(Long id) {
        Celula celula = celulaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada com o ID: " + id));

        return celula.getMembros();  // retorna todos
        // Ou, se quiser só ativos aqui:
        // return celula.getMembros().stream()
        //         .filter(m -> m.getStatus() == StatusMembro.ATIVO)
        //         .toList();
    }
    // Método 2: só ativos (recomendado para a maioria dos usos)
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

        // Força o load
        Hibernate.initialize(celula.getMembros());

        System.out.println("DEBUG CELULA SERVICE - Membros na memória: " + celula.getMembros().size());
        celula.getMembros().forEach(m -> System.out.println("  -> Membro: " + m.getNome() +
                " | Status: " + m.getStatus() +
                " | ID: " + m.getId()));

        return celula;
    }

    @Transactional(readOnly = true)
    public List<CelulaResumoDTO> buscarSolicitacoesPendentes() {
        // Busca apenas células que estão aguardando análise da secretaria
        return celulaRepository.findByStatusMultiplicacao(Celula.StatusMultiplicacao.EM_ANALISE)
                .stream()
                .map(CelulaResumoDTO::new) // Usa o construtor do record acima
                .toList();

    }
    @Transactional
    public void vincularMembro(Long celulaId, Long membroId) {
        Celula celula = celulaRepository.findById(celulaId).orElseThrow();
        Membro membro = membroRepository.findById(membroId).orElseThrow();

        membro.setCelula(celula);
        celula.getMembros().add(membro);

        celulaRepository.save(celula); // Salva o vínculo

        // AQUI ESTÁ O SEGREDO: Chama o seu método automático
        this.verificarELancarAlertaMultiplicacao(celulaId);
    }

    @Transactional
    public void decidirMultiplicacao(Long celulaId, boolean aprovado) {
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada"));

        if (aprovado) {
            celula.setStatusMultiplicacao(Celula.StatusMultiplicacao.APROVADO);
            // Aqui você poderia disparar a criação da nova célula se quiser automatizar tudo
        } else {
            celula.setStatusMultiplicacao(Celula.StatusMultiplicacao.REJEITADO);
        }

        celulaRepository.save(celula);

        // 1. Criamos um título dinâmico
        String titulo = aprovado ? "Multiplicação APROVADA" : "Solicitação Indeferida";

        // 2. Ajustamos a mensagem
        String msg = aprovado
                ? "🎉 Parabéns! Sua solicitação de multiplicação para a célula " + celula.getNome() + " foi APROVADA!"
                : "⚠️ Olá líder, sua solicitação de multiplicação para a célula " + celula.getNome() + " foi indeferida no momento.";

        // 3. Chamada corrigida com os 4 parâmetros: ID, Título, Mensagem, Tipo
        notificacaoService.enviarNotificacao(
                celula.getLider().getId(),
                titulo,
                msg,
                Notificacao.TipoNotificacao.MULTIPLICACAO_CELULA
        );
    }
    @Transactional
    public CelulaStatusMultiplicacaoDTO atualizarStatusMultiplicacao(Long id, boolean aprovado) {

        Celula celula = celulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        celula.setStatusMultiplicacao(
                aprovado
                        ? Celula.StatusMultiplicacao.APROVADO
                        : Celula.StatusMultiplicacao.REJEITADO
        );

        celulaRepository.save(celula);

        return new CelulaStatusMultiplicacaoDTO(
                celula.getId(),
                celula.getStatusMultiplicacao().name()
        );
    }
    @Transactional(readOnly = true)
    public List<MembroResponseDTO> buscarMembrosPorCelula(Long celulaId) {
        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));
        return celula.getMembros()
                .stream()
                .map(MembroResponseDTO::new)
                .collect(Collectors.toList());
    }
}
