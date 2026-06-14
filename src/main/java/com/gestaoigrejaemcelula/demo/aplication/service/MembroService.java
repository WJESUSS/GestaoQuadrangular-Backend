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
    private void addDiff(Map<String, Object> diff, String campo, Object antes, Object depois) {
        if (!Objects.equals(antes, depois))
            diff.put(campo, Map.of("de", str(antes), "para", str(depois)));
    }
    @Transactional
    public MembroResponseDTO atualizar(Long id, MembroRequestDTO dto) {
        Membro m = buscarEntidadePorId(id);
        Map<String, Object> diff = new LinkedHashMap<>();

        // Dados básicos
        addDiff(diff, "nome",                  m.getNome(),                  dto.getNome());
        addDiff(diff, "telefone",              m.getTelefone(),              dto.getTelefone());
        addDiff(diff, "email",                 m.getEmail(),                 dto.getEmail());
        addDiff(diff, "status",                m.getStatus(),                dto.getStatus());
        addDiff(diff, "estadoCivil",           m.getEstadoCivil(),           dto.getEstadoCivil());

        // Filiação
        addDiff(diff, "nomeConjuge",           m.getNomeConjuge(),           dto.getNomeConjuge());
        addDiff(diff, "nomeMae",               m.getNomeMae(),               dto.getNomeMae());
        addDiff(diff, "nomePai",               m.getNomePai(),               dto.getNomePai());

        // Endereço
        addDiff(diff, "endereco",              m.getEndereco(),              dto.getEndereco());
        addDiff(diff, "bairro",                m.getBairro(),                dto.getBairro());
        addDiff(diff, "cidade",                m.getCidade(),                dto.getCidade());
        addDiff(diff, "cep",                   m.getCep(),                   dto.getCep());

        // Profissão / Escolaridade
        addDiff(diff, "profissao",             m.getProfissao(),             dto.getProfissao());
        addDiff(diff, "grauEscolaridade",      m.getGrauEscolaridade(),      dto.getGrauEscolaridade());

        // Dados espirituais
        addDiff(diff, "batizadoNasAguas",      m.getBatizadoNasAguas(),      dto.getBatizadoNasAguas());
        addDiff(diff, "batizadoEspiritoSanto", m.getBatizadoEspiritoSanto(), dto.getBatizadoEspiritoSanto());

        // Datas
        addDiff(diff, "dataNascimento",        m.getDataNascimento(),        dto.getDataNascimento());
        addDiff(diff, "dataConversao",         m.getDataConversao(),         dto.getDataConversao());
        addDiff(diff, "dataBatismo",           m.getDataBatismo(),           dto.getDataBatismo());

        copiarDtoParaEntidade(dto, m);
        Membro salvo = repository.save(m);

        if (!diff.isEmpty())
            auditoria.registrar("MEMBRO", salvo.getId(), salvo.getNome(), "UPDATE", diff);

        return new MembroResponseDTO(salvo);
    }
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
        membro.setNomeConjuge(dto.getNomeConjuge());
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
        membro.setUf(dto.getUf());

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