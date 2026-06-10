package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.HistoricoStatusMembro;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.Tipo;
import com.gestaoigrejaemcelula.demo.domain.repository.HistoricoStatusMembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.VisitanteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MembroService {

    private final MembroRepository repository;
    private final HistoricoStatusMembroRepository historicoRepository;
    private final VisitanteRepository visitanteRepository;
    private final AuditoriaHelper auditoria;

    public MembroService(MembroRepository repository,
                         HistoricoStatusMembroRepository historicoRepository,
                         VisitanteRepository visitanteRepository,
                         AuditoriaHelper auditoria) {
        this.repository = repository;
        this.historicoRepository = historicoRepository;
        this.visitanteRepository = visitanteRepository;
        this.auditoria = auditoria;
    }

    // -------------------------------------------------------
    // LISTAGEM POR CÉLULA
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<MembroCelulaDTO> listarMembrosPorCelula(Long celulaId) {
        return repository.findByCelulaId(celulaId)
                .stream()
                .map(m -> {
                    MembroCelulaDTO dto = new MembroCelulaDTO();
                    dto.setId(m.getId());
                    dto.setNome(m.getNome());
                    dto.setTelefone(m.getTelefone());
                    dto.setStatus(m.getStatus().getDescricao());
                    dto.setTipo(Tipo.MEMBRO);
                    return dto;
                }).toList();
    }

    // -------------------------------------------------------
    // CRIAÇÃO E ATUALIZAÇÃO
    // -------------------------------------------------------

    public MembroResponseDTO criar(MembroRequestDTO dto) {
        Membro membro = new Membro();
        copiarDtoParaEntidade(dto, membro);
        Membro salvo = repository.save(membro);
        auditoria.registrar("MEMBRO", salvo.getId(), salvo.getNome(), "CREATE", null);
        return new MembroResponseDTO(salvo);
    }

    @Transactional
    public MembroResponseDTO atualizar(Long id, MembroRequestDTO dto) {
        Membro membro = buscarEntidadePorId(id);

        // Diff dos campos principais para auditoria
        Map<String, Object> diff = new LinkedHashMap<>();
        if (!Objects.equals(membro.getNome(), dto.getNome()))
            diff.put("nome", Map.of("de", str(membro.getNome()), "para", str(dto.getNome())));
        if (!Objects.equals(membro.getTelefone(), dto.getTelefone()))
            diff.put("telefone", Map.of("de", str(membro.getTelefone()), "para", str(dto.getTelefone())));
        if (!Objects.equals(membro.getEmail(), dto.getEmail()))
            diff.put("email", Map.of("de", str(membro.getEmail()), "para", str(dto.getEmail())));
        if (!Objects.equals(membro.getStatus(), dto.getStatus()))
            diff.put("status", Map.of("de", str(membro.getStatus()), "para", str(dto.getStatus())));
        if (!Objects.equals(membro.getEndereco(), dto.getEndereco()))
            diff.put("endereco", Map.of("de", str(membro.getEndereco()), "para", str(dto.getEndereco())));
        if (!Objects.equals(membro.getEstadoCivil(), dto.getEstadoCivil()))
            diff.put("estadoCivil", Map.of("de", str(membro.getEstadoCivil()), "para", str(dto.getEstadoCivil())));

        copiarDtoParaEntidade(dto, membro);
        Membro salvo = repository.save(membro);

        if (!diff.isEmpty())
            auditoria.registrar("MEMBRO", salvo.getId(), salvo.getNome(), "UPDATE", diff);

        return new MembroResponseDTO(salvo);
    }

    // -------------------------------------------------------
    // LISTAGENS
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<MembroResumoDTO> listarSemCelula() {
        return repository.findByCelulaIsNull()
                .stream()
                .map(membro -> {
                    MembroResumoDTO dto = new MembroResumoDTO();
                    dto.setId(membro.getId());
                    dto.setNome(membro.getNome());
                    dto.setTelefone(membro.getTelefone());
                    dto.setStatus(membro.getStatus().getDescricao());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MembroResponseDTO> listarTodos() {
        return repository.findAll().stream().map(MembroResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public MembroResponseDTO buscarPorId(Long id) {
        return new MembroResponseDTO(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<MembroResponseDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome).stream().map(MembroResponseDTO::new).toList();
    }

    public void remover(Long id) {
        Membro m = buscarEntidadePorId(id);
        repository.deleteById(id);
        auditoria.registrar("MEMBRO", id, m.getNome(), "DELETE", null);
    }

    // -------------------------------------------------------
    // MAPEAMENTO DTO → ENTIDADE
    // -------------------------------------------------------

    private void copiarDtoParaEntidade(MembroRequestDTO dto, Membro membro) {

        // Dados básicos
        membro.setNome(dto.getNome());
        membro.setTelefone(dto.getTelefone());
        membro.setEmail(dto.getEmail());
        membro.setStatus(dto.getStatus());
        membro.setEstadoCivil(dto.getEstadoCivil());
        membro.setDataNascimento(dto.getDataNascimento());
        membro.setDataConversao(dto.getDataConversao());
        membro.setDataBatismo(dto.getDataBatismo());

        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            membro.setCpf(dto.getCpf());
        }
        if (dto.getRg() != null && !dto.getRg().isBlank()) {
            membro.setRg(dto.getRg());
        }

        // Filiação e naturalidade
        membro.setNomeMae(dto.getNomeMae());
        membro.setNomePai(dto.getNomePai());
        membro.setNomeCônjuge(dto.getNomeCônjuge());
        membro.setNaturalidade(dto.getNaturalidade());

        // Escolaridade e profissão
        membro.setGrauEscolaridade(dto.getGrauEscolaridade());
        membro.setCurso(dto.getCurso());
        membro.setProfissao(dto.getProfissao());

        // Endereço detalhado
        membro.setEndereco(dto.getEndereco());
        membro.setNumero(dto.getNumero());
        membro.setBairro(dto.getBairro());
        membro.setCidade(dto.getCidade());
        membro.setCep(dto.getCep());

        // Dados espirituais
        membro.setPertenceOutraReligiao(dto.getPertenceOutraReligiao());
        membro.setQualReligiao(dto.getQualReligiao());
        membro.setBatizadoNasAguas(dto.getBatizadoNasAguas());
        membro.setDataBatizadoNasAguas(dto.getDataBatizadoNasAguas());
        membro.setIgrejaBatizadoNasAguas(dto.getIgrejaBatizadoNasAguas());
        membro.setBatizadoEspiritoSanto(dto.getBatizadoEspiritoSanto());

        // Arrolamento
        membro.setTipoArrolamento(dto.getTipoArrolamento());
        membro.setJurisdicaoArrolamento(dto.getJurisdicaoArrolamento());
        membro.setArroladoPor(dto.getArroladoPor());

        // Observações
        membro.setObservacoes(dto.getObservacoes());
    }

    // -------------------------------------------------------
    // STATUS
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<MembroResponseDTO> listarPorStatus(StatusMembro status) {
        return repository.findByStatus(status)
                .stream()
                .map(MembroResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void alterarStatus(Long membroId, StatusMembro novoStatus, String observacao) {
        Membro membro = repository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        StatusMembro statusAnterior = membro.getStatus();
        membro.setStatus(novoStatus);

        if (novoStatus.deveRemoverVinculos()) removerVinculos(membro);

        repository.save(membro);
        registrarHistorico(membro, statusAnterior, novoStatus, observacao);

        auditoria.registrar("MEMBRO", membroId, membro.getNome(), "UPDATE",
                Map.of("status", Map.of("de", str(statusAnterior), "para", str(novoStatus))));
    }

    // -------------------------------------------------------
    // AUXILIARES
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<MembroResumoDTO> listarTodosAtivos() {
        return repository.listarAtivosOrdenados();
    }

    public List<MembroSelectDTO> listarParaSelect() {
        return repository.listarParaSelect();
    }

    @Transactional(readOnly = true)
    public List<AlertaDTO> obterAlertasCriticos() {
        LocalDate dataLimite = LocalDate.now().minusDays(21);
        return repository.findAlertasMembros(dataLimite).stream()
                .filter(alerta -> alerta.getTotalFaltas() != null && alerta.getTotalFaltas() >= 2)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CelulaResponseDTO.MembroDTO> buscarAniversariantesHoje(Long celulaId) {
        int dia = LocalDate.now().getDayOfMonth();
        int mes = LocalDate.now().getMonthValue();

        return repository.findByCelulaId(celulaId)
                .stream()
                .filter(m -> m.getDataNascimento() != null
                        && m.getDataNascimento().getDayOfMonth() == dia
                        && m.getDataNascimento().getMonthValue() == mes)
                .map(m -> new CelulaResponseDTO.MembroDTO(
                        m.getId(),
                        m.getNome(),
                        m.getTelefone(),
                        m.getDataNascimento()
                ))
                .collect(Collectors.toList());
    }

    private Membro buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));
    }

    private void removerVinculos(Membro membro) {
        membro.setCelula(null);
    }

    private void registrarHistorico(Membro membro, StatusMembro anterior,
                                    StatusMembro novo, String observacao) {
        HistoricoStatusMembro historico = new HistoricoStatusMembro();
        historico.setMembro(membro);
        historico.setStatusAnterior(anterior);
        historico.setStatusNovo(novo);
        historico.setDataAlteracao(LocalDateTime.now());
        historico.setObservacao(observacao);
        historicoRepository.save(historico);
    }

    private String str(Object o) { return o != null ? o.toString() : ""; }
}