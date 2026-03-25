package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.HistoricoStatusMembro;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.Tipo;
import com.gestaoigrejaemcelula.demo.domain.repository.HistoricoStatusMembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.VisitanteRepository; // Import necessário
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembroService {

    private final MembroRepository repository;
    private final HistoricoStatusMembroRepository historicoRepository;
    private  final  VisitanteRepository visitanteRepository;

    public MembroService(MembroRepository repository,
                         HistoricoStatusMembroRepository historicoRepository, VisitanteRepository visitanteRepository) {
        this.repository = repository;
        this.historicoRepository = historicoRepository;
        this.visitanteRepository = visitanteRepository;
    }



    /**
     * MÉTODO UNIFICADO: Busca Membros e Visitantes da mesma célula
     * Isso resolve o erro de duplicidade e traz todos para a chamada.
     */
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

    // --- MÉTODOS DE CRIAÇÃO E ATUALIZAÇÃO ---

    public MembroResponseDTO criar(MembroRequestDTO dto) {
        Membro membro = new Membro();
        copiarDtoParaEntidade(dto, membro);
        return new MembroResponseDTO(repository.save(membro));
    }

    public MembroResponseDTO atualizar(Long id, MembroRequestDTO dto) {
        Membro membro = buscarEntidadePorId(id);
        copiarDtoParaEntidade(dto, membro);
        return new MembroResponseDTO(repository.save(membro));
    }

    // --- MÉTODOS DE LISTAGEM ---

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

    public List<MembroResponseDTO> listarTodos() {
        return repository.findAll().stream().map(MembroResponseDTO::new).toList();
    }

    public MembroResponseDTO buscarPorId(Long id) {
        return new MembroResponseDTO(buscarEntidadePorId(id));
    }

    public List<MembroResponseDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome).stream().map(MembroResponseDTO::new).toList();
    }

    public void remover(Long id) {
        repository.deleteById(id);
    }

    // --- MÉTODOS AUXILIARES ---

// --- MÉTODOS AUXILIARES ---

    private void copiarDtoParaEntidade(MembroRequestDTO dto, Membro membro) {

        membro.setNome(dto.getNome());
        membro.setTelefone(dto.getTelefone());
        membro.setEmail(dto.getEmail());
        membro.setStatus(dto.getStatus());

        membro.setEndereco(dto.getEndereco());
        membro.setDataNascimento(dto.getDataNascimento());
        membro.setDataConversao(dto.getDataConversao());
        membro.setDataBatismo(dto.getDataBatismo());

        // CPF opcional
        if(dto.getCpf() != null && !dto.getCpf().isBlank()){
            membro.setCpf(dto.getCpf());
        }

        membro.setEstadoCivil(dto.getEstadoCivil());
    }


    private Membro buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));
    }
    // Adicione este método dentro da classe MembroService
    public List<MembroResponseDTO> listarPorStatus(StatusMembro status) {
        return repository.findByStatus(status)
                .stream()
                .map(MembroResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MembroResumoDTO> listarTodosAtivos() {
        return repository.findAll()
                .stream()
                .map(membro -> new MembroResumoDTO(membro.getId(), membro.getNome()))
                // Mude de a.nome() para a.getNome()
                .sorted((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()))
                .collect(Collectors.toList());
    }
    public List<MembroSelectDTO> listarParaSelect() {
        return repository.listarParaSelect();
    }





    @Transactional
    public void alterarStatus(Long membroId,
                              StatusMembro novoStatus,
                              String observacao) {

        Membro membro = repository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        StatusMembro statusAnterior = membro.getStatus();

        membro.setStatus(novoStatus);

        if (novoStatus.deveRemoverVinculos()) {
            removerVinculos(membro);
        }

        repository.save(membro);

        registrarHistorico(membro, statusAnterior, novoStatus, observacao);
    }

    private void removerVinculos(Membro membro) {

        // Remove da célula
        membro.setCelula(null);

        // Remove departamentos

    }

    private void registrarHistorico(Membro membro,
                                    StatusMembro anterior,
                                    StatusMembro novo,
                                    String observacao) {

        HistoricoStatusMembro historico = new HistoricoStatusMembro();
        historico.setMembro(membro);
        historico.setStatusAnterior(anterior);
        historico.setStatusNovo(novo);
        historico.setDataAlteracao(LocalDateTime.now());
        historico.setObservacao(observacao);

        historicoRepository.save(historico);
    }

}