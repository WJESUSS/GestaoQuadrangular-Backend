package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.VisitanteResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.repository.VisitanteRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitanteService {

    private final VisitanteRepository repository;
    private final CelulaRepository celulaRepository;

    public VisitanteService(VisitanteRepository repository, CelulaRepository celulaRepository) {
        this.repository = repository;
        this.celulaRepository = celulaRepository;
    }

    @Transactional
    public VisitanteResponseDTO cadastrar(VisitanteRequestDTO dto) {
        Visitante visitante = new Visitante();
        preencher(visitante, dto);

        // Garante os valores iniciais de um novo visitante para evitar duplicidade no Front
        visitante.setAtivo(true);

        return toDTO(repository.save(visitante));
    }

    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VisitanteResponseDTO atualizar(Long id, VisitanteRequestDTO dto) {
        Visitante visitante = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitante não encontrado"));

        preencher(visitante, dto);
        return toDTO(repository.save(visitante));
    }
    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarVisitantesPorCelula(Long celulaId) {
        return repository.findByCelulaIdAndAtivoTrue(celulaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    /**
     * Preenche a entidade com os dados do DTO e realiza o vínculo com a Célula
     */
    private void preencher(Visitante visitante, VisitanteRequestDTO dto) {
        visitante.setNome(dto.getNome());
        visitante.setTelefone(dto.getTelefone());
        visitante.setEmail(dto.getEmail());
        visitante.setDataPrimeiraVisita(dto.getDataPrimeiraVisita());
        visitante.setOrigem(dto.getOrigem());
        visitante.setResponsavelAcompanhamento(dto.getResponsavelAcompanhamento());

        // Se o DTO trouxer uma informação de ativo, respeitamos, senão padrão true
        visitante.setAtivo(dto.isAtivo());

        if (dto.getCelulaId() != null) {
            Celula celula = celulaRepository.findById(dto.getCelulaId())
                    .orElseThrow(() -> new RuntimeException("Célula não encontrada com ID: " + dto.getCelulaId()));
            visitante.setCelula(celula);
        }
    }

    /**
     * CENTRALIZADOR DE CONVERSÃO: Garante que o Front receba VisitanteResponseDTO purinho
     */
    private VisitanteResponseDTO toDTO(Visitante visitante) {
        VisitanteResponseDTO dto = new VisitanteResponseDTO();
        dto.setId(visitante.getId());
        dto.setNome(visitante.getNome());
        dto.setTelefone(visitante.getTelefone());
        dto.setEmail(visitante.getEmail());
        dto.setDataPrimeiraVisita(visitante.getDataPrimeiraVisita());
        dto.setOrigem(visitante.getOrigem());
        dto.setResponsavelAcompanhamento(visitante.getResponsavelAcompanhamento());

        // Crucial: define explicitamente os campos de status
        dto.setAtivo(visitante.isAtivo());

        return dto;
    }

    private Celula buscarPorId(Long celulaId) {
        return celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));
    }
    @Transactional(readOnly = true)
    public List<VisitanteResponseDTO> listarAtivosPorCelula(Long celulaId) {
        return repository.findByCelulaIdAndAtivoTrue(celulaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}