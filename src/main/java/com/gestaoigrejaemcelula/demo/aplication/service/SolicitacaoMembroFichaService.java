package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.DecisaoSolicitacaoMembroDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.SolicitacaoMembroFichaRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.SolicitacaoMembroFichaResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.SolicitacaoMembroFicha;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.CargoMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusSolicitacaoMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoArrolamento;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.SolicitacaoMembroFichaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import com.gestaoigrejaemcelula.demo.web.handler.BusinessException;
import com.gestaoigrejaemcelula.demo.web.handler.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class SolicitacaoMembroFichaService {

    private static final String ENTIDADE = "SOLICITACAO_MEMBRO";

    private final SolicitacaoMembroFichaRepository solicitacaoRepository;
    private final MembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository;
    private final CelulaRepository celulaRepository;
    private final AuditoriaHelper auditoria;

    public SolicitacaoMembroFichaService(
            SolicitacaoMembroFichaRepository solicitacaoRepository,
            MembroRepository membroRepository,
            UsuarioRepository usuarioRepository,
            CelulaRepository celulaRepository,
            AuditoriaHelper auditoria) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.membroRepository = membroRepository;
        this.usuarioRepository = usuarioRepository;
        this.celulaRepository = celulaRepository;
        this.auditoria = auditoria;
    }

    // -------------------------------------------------------
    // LÍDER: Envia ficha de solicitação
    // -------------------------------------------------------

    @Transactional
    public SolicitacaoMembroFichaResponseDTO enviarFicha(SolicitacaoMembroFichaRequestDTO dto) {
        Usuario lider = getUsuarioLogado();

        SolicitacaoMembroFicha ficha = new SolicitacaoMembroFicha();
        ficha.setLider(lider);

        // Associa a célula do próprio líder automaticamente
        if (lider.getCelula() != null) {
            ficha.setCelula(lider.getCelula());
        }

        copiarDtoParaFicha(dto, ficha);

        SolicitacaoMembroFicha salva = solicitacaoRepository.save(ficha);

        auditoria.registrar(ENTIDADE, salva.getId(), salva.getNome(),
                "CREATE", Map.of("lider", lider.getNome(), "status", "PENDENTE"));

        return new SolicitacaoMembroFichaResponseDTO(salva);
    }

    // -------------------------------------------------------
    // LÍDER: Consulta suas próprias solicitações
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<SolicitacaoMembroFichaResponseDTO> listarMinhasSolicitacoes() {
        Usuario lider = getUsuarioLogado();
        return solicitacaoRepository
                .findByLiderIdOrderByDataSolicitacaoDesc(lider.getId())
                .stream()
                .map(SolicitacaoMembroFichaResponseDTO::new)
                .toList();
    }

    // -------------------------------------------------------
    // SECRETARIA: Lista solicitações pendentes
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<SolicitacaoMembroFichaResponseDTO> listarPendentes(Pageable pageable) {
        return solicitacaoRepository
                .findByStatus(StatusSolicitacaoMembro.PENDENTE, pageable)
                .map(SolicitacaoMembroFichaResponseDTO::new);
    }

    // -------------------------------------------------------
    // SECRETARIA: Lista todas as solicitações (qualquer status)
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<SolicitacaoMembroFichaResponseDTO> listarTodas(Pageable pageable) {
        return solicitacaoRepository.findAll(pageable)
                .map(SolicitacaoMembroFichaResponseDTO::new);
    }

    // -------------------------------------------------------
    // SECRETARIA: Busca uma solicitação por ID
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public SolicitacaoMembroFichaResponseDTO buscarPorId(Long id) {
        return new SolicitacaoMembroFichaResponseDTO(buscarEntidade(id));
    }

    // -------------------------------------------------------
    // SECRETARIA: Decide (aprovar ou rejeitar)
    // -------------------------------------------------------

    @Transactional
    public SolicitacaoMembroFichaResponseDTO decidir(Long id, DecisaoSolicitacaoMembroDTO decisao) {
        SolicitacaoMembroFicha ficha = buscarEntidade(id);

        if (ficha.getStatus() != StatusSolicitacaoMembro.PENDENTE) {
            throw new BusinessException("Esta solicitação já foi processada: " + ficha.getStatus().getDescricao());
        }

        if (!decisao.isAprovado() &&
                (decisao.getMotivoRejeicao() == null || decisao.getMotivoRejeicao().isBlank())) {
            throw new BusinessException("Motivo da rejeição é obrigatório ao rejeitar.");
        }

        Usuario secretario = getUsuarioLogado();
        ficha.setSecretario(secretario);
        ficha.setDataDecisao(LocalDateTime.now());

        if (decisao.isAprovado()) {
            // ✅ APROVADO: cria o membro de verdade
            Membro membro = criarMembroDaFicha(ficha);
            ficha.setStatus(StatusSolicitacaoMembro.APROVADO);
            ficha.setMembroCriadoId(membro.getId());

            auditoria.registrar(ENTIDADE, ficha.getId(), ficha.getNome(),
                    "APROVADO",
                    Map.of("secretario", secretario.getNome(), "membroCriadoId", membro.getId()));
        } else {
            // ❌ REJEITADO: só registra o motivo
            ficha.setStatus(StatusSolicitacaoMembro.REJEITADO);
            ficha.setMotivoRejeicao(decisao.getMotivoRejeicao());

            auditoria.registrar(ENTIDADE, ficha.getId(), ficha.getNome(),
                    "REJEITADO",
                    Map.of("secretario", secretario.getNome(), "motivo", decisao.getMotivoRejeicao()));
        }

        return new SolicitacaoMembroFichaResponseDTO(solicitacaoRepository.save(ficha));
    }

    // -------------------------------------------------------
    // PRIVADOS
    // -------------------------------------------------------

    /** Cria um Membro real a partir dos dados da ficha aprovada */
    private Membro criarMembroDaFicha(SolicitacaoMembroFicha ficha) {
        validarIdadeEBatismo(ficha);

        Membro membro = new Membro();

        membro.setNome(ficha.getNome());
        membro.setTelefone(ficha.getTelefone());
        membro.setEmail(ficha.getEmail());
        membro.setCpf(ficha.getCpf());
        membro.setRg(ficha.getRg());
        membro.setEstadoCivil(ficha.getEstadoCivil());
        membro.setDataNascimento(ficha.getDataNascimento());
        membro.setDataConversao(ficha.getDataConversao());
        membro.setDataBatismo(ficha.getDataBatismo());
        membro.setNomeMae(ficha.getNomeMae());
        membro.setNomePai(ficha.getNomePai());
        membro.setNomeConjuge(ficha.getNomeConjuge());
        membro.setNaturalidade(ficha.getNaturalidade());
        membro.setGrauEscolaridade(ficha.getGrauEscolaridade());
        membro.setCurso(ficha.getCurso());
        membro.setProfissao(ficha.getProfissao());
        membro.setEndereco(ficha.getEndereco());
        membro.setNumero(ficha.getNumero());
        membro.setBairro(ficha.getBairro());
        membro.setCidade(ficha.getCidade());
        membro.setCep(ficha.getCep());
        membro.setUf(ficha.getUf());
        membro.setPertenceOutraReligiao(ficha.getPertenceOutraReligiao());
        membro.setQualReligiao(ficha.getQualReligiao());
        membro.setBatizadoNasAguas(ficha.getBatizadoNasAguas());
        membro.setDataBatizadoNasAguas(ficha.getDataBatizadoNasAguas());
        membro.setIgrejaBatizadoNasAguas(ficha.getIgrejaBatizadoNasAguas());
        membro.setBatizadoEspiritoSanto(ficha.getBatizadoEspiritoSanto());
        membro.setTipoArrolamento(ficha.getTipoArrolamento());
        membro.setJurisdicaoArrolamento(ficha.getJurisdicaoArrolamento());
        membro.setArroladoPor(ficha.getArroladoPor());
        membro.setCargos(ficha.getCargos() != null ? ficha.getCargos() : new HashSet<>());
        membro.setObservacoes(ficha.getObservacoes());

        // Vincula à célula do líder que enviou a ficha
        membro.setCelula(ficha.getCelula());

        // Status padrão = ATIVO
        membro.setStatus(StatusMembro.ATIVO);

        Membro salvo = membroRepository.save(membro);

        auditoria.registrar("MEMBRO", salvo.getId(), salvo.getNome(), "CREATE",
                Map.of("origem", ENTIDADE, "solicitacaoId", ficha.getId()));

        return salvo;
    }

    private void validarIdadeEBatismo(SolicitacaoMembroFicha ficha) {
        if (ficha.getDataNascimento() == null) return;

        int idade = Period.between(ficha.getDataNascimento(), LocalDate.now()).getYears();

        boolean isTransferencia = TipoArrolamento.TRANSFERENCIA.equals(ficha.getTipoArrolamento());
        boolean isBatizado = Boolean.TRUE.equals(ficha.getBatizadoNasAguas());

        if (idade < 12 && !isBatizado) {
            throw new BusinessException(
                    "Não é permitido cadastrar menor de 12 anos que não é batizado nas águas.");
        }

        if (idade >= 12 && !isBatizado && !isTransferencia) {
            throw new BusinessException(
                    "Não é permitido cadastrar membro sem batismo nas águas ou transferência.");
        }
    }

    private void copiarDtoParaFicha(SolicitacaoMembroFichaRequestDTO dto, SolicitacaoMembroFicha ficha) {
        ficha.setNome(dto.getNome());
        ficha.setTelefone(dto.getTelefone());
        ficha.setEmail(dto.getEmail());
        ficha.setCpf(dto.getCpf());
        ficha.setRg(dto.getRg());
        ficha.setEstadoCivil(dto.getEstadoCivil());
        ficha.setDataNascimento(dto.getDataNascimento());
        ficha.setDataConversao(dto.getDataConversao());
        ficha.setDataBatismo(dto.getDataBatismo());
        ficha.setNomeMae(dto.getNomeMae());
        ficha.setNomePai(dto.getNomePai());
        ficha.setNomeConjuge(dto.getNomeConjuge());
        ficha.setNaturalidade(dto.getNaturalidade());
        ficha.setGrauEscolaridade(dto.getGrauEscolaridade());
        ficha.setCurso(dto.getCurso());
        ficha.setProfissao(dto.getProfissao());
        ficha.setEndereco(dto.getEndereco());
        ficha.setNumero(dto.getNumero());
        ficha.setBairro(dto.getBairro());
        ficha.setCidade(dto.getCidade());
        ficha.setCep(dto.getCep());
        ficha.setUf(dto.getUf());
        ficha.setPertenceOutraReligiao(dto.getPertenceOutraReligiao());
        ficha.setQualReligiao(dto.getQualReligiao());
        ficha.setBatizadoNasAguas(dto.getBatizadoNasAguas());
        ficha.setDataBatizadoNasAguas(dto.getDataBatizadoNasAguas());
        ficha.setIgrejaBatizadoNasAguas(dto.getIgrejaBatizadoNasAguas());
        ficha.setBatizadoEspiritoSanto(dto.getBatizadoEspiritoSanto());
        ficha.setTipoArrolamento(dto.getTipoArrolamento());
        ficha.setJurisdicaoArrolamento(dto.getJurisdicaoArrolamento());
        ficha.setArroladoPor(dto.getArroladoPor());
        ficha.setCargos(dto.getCargos() != null ? dto.getCargos() : new HashSet<>());
        ficha.setObservacoes(dto.getObservacoes());
    }

    private SolicitacaoMembroFicha buscarEntidade(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada: " + id));
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado"));
    }
}