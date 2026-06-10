package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.*;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.enums.JustificativaFalta;
import com.gestaoigrejaemcelula.demo.domain.repository.*;
import com.gestaoigrejaemcelula.demo.web.handler.BusinessException;
import com.gestaoigrejaemcelula.demo.web.handler.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private PresencaRepository presencaRepository;
    @Autowired
    private RelatorioRepository relatorioRepository;
    @Autowired
    private CelulaRepository celulaRepository;
    @Autowired
    private MembroRepository membroRepository;
    @Autowired
    private VisitanteRepository visitanteRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private DiscipuladoRelatorioRepository discipuladoRelatorioRepository;
    @Autowired
    private RankingCelulaService rankingCelulaService;

    private boolean safe(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    /* =========================
       SALVAR RELATÓRIO COMPLETO
       ========================= */

    @Transactional
    public void salvarRelatorio(@Valid RelatorioRequestDTO dto) throws AccessDeniedException {

        Usuario lider = usuarioService.getUsuarioLogado();

        Celula celula = celulaRepository.findByLider_Id(lider.getId())
                .orElseThrow(() -> new RuntimeException("Líder não possui célula vinculada"));

        Relatorio relatorio = new Relatorio();
        relatorio.setCelula(celula);
        relatorio.setDataReuniao(dto.getDataReuniao());
        relatorio.setEstudo(dto.getEstudo());

        relatorio.setQuantidadeVisitantes(
                dto.getQuantidadeVisitantes() != null ? dto.getQuantidadeVisitantes() : 0
        );

        if (dto.getMembrosPresentesIds() != null && !dto.getMembrosPresentesIds().isEmpty()) {
            List<Membro> membros = membroRepository.findAllById(dto.getMembrosPresentesIds());
            if (membros.size() != dto.getMembrosPresentesIds().size()) {
                throw new IllegalArgumentException("Um ou mais membros não existem");
            }
            relatorio.setPresentes(membros);
        }

        if (dto.getVisitantesPresentes() != null && !dto.getVisitantesPresentes().isEmpty()) {
            List<Long> ids = dto.getVisitantesPresentes()
                    .stream()
                    .map(v -> v.getId())
                    .toList();

            List<Visitante> visitantes = visitanteRepository.findAllById(ids);

            for (Visitante visitante : visitantes) {
                dto.getVisitantesPresentes().stream()
                        .filter(v -> v.getId().equals(visitante.getId()))
                        .findFirst()
                        .ifPresent(vdto -> {
                            visitante.setDecisaoEspiritual(
                                    vdto.getDecisaoEspiritual() != null
                                            ? vdto.getDecisaoEspiritual()
                                            : DecisaoEspiritual.NENHUMA
                            );
                        });
            }

            visitanteRepository.saveAll(visitantes);
            visitanteRepository.flush();
            relatorio.setVisitantesPresentes(visitantes);
        }

        relatorioRepository.save(relatorio);

        // Salvar membros ausentes com justificativa
        salvarMembrosAusentes(dto, relatorio);
    }

    /* =========================
       LISTAGENS
       ========================= */

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> listarRelatoriosUltimosSeteDias() {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        LocalDate hoje = LocalDate.now(zoneId);
        LocalDate seteDiasAtras = hoje.minusDays(7);

        return relatorioRepository
                .findByDataReuniaoGreaterThanEqual(seteDiasAtras)
                .stream()
                .sorted(Comparator.comparing(Relatorio::getDataReuniao).reversed())
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> listarTodosComoDTO() {
        return relatorioRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> listarPorCelula(Long celulaId) {
        return relatorioRepository.findByCelulaIdOrderByDataReuniaoDesc(celulaId)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> listarHistoricoDaMinhaCelula(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Celula celula;

        Optional<Celula> celulaComoLider = celulaRepository.findByLider_Id(usuario.getId());

        if (celulaComoLider.isPresent()) {
            celula = celulaComoLider.get();
        } else if (usuario.getCelula() != null) {
            celula = usuario.getCelula();
        } else {
            return List.of();
        }

        return relatorioRepository.findByCelulaIdOrderByDataReuniaoDesc(celula.getId())
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    /* =========================
       RESUMO SEMANAL
       ========================= */

    @Transactional(readOnly = true)
    public RelatorioResumoDTO buscarResumoSemana(LocalDate inicio, LocalDate fim) {

        List<Relatorio> realizadas = relatorioRepository
                .findByDataReuniaoBetween(inicio, fim)
                .stream()
                .filter(r -> r.getRealizada() == null || r.getRealizada())
                .toList();

        List<Relatorio> naoRealizadas = relatorioRepository
                .findByRealizadaFalseAndDataCadastroBetween(
                        inicio.atStartOfDay(),
                        fim.plusDays(1).atStartOfDay()
                );

        List<Relatorio> todos = new java.util.ArrayList<>(realizadas);
        naoRealizadas.forEach(nr -> {
            if (todos.stream().noneMatch(r -> r.getId().equals(nr.getId()))) {
                todos.add(nr);
            }
        });

        int totalMembros    = realizadas.stream().mapToInt(Relatorio::getQuantidadeMembros).sum();
        int totalVisitantes = realizadas.stream().mapToInt(Relatorio::getTotalVisitantes).sum();
        int totalCelulas    = (int) realizadas.stream()
                .map(r -> r.getCelula().getId()).distinct().count();

        RelatorioResumoDTO dto = new RelatorioResumoDTO();
        dto.setInicio(inicio);
        dto.setFim(fim);
        dto.setTotalCelulas(totalCelulas);
        dto.setTotalMembros(totalMembros);
        dto.setTotalVisitantes(totalVisitantes);
        dto.setRelatorios(todos.stream().map(this::converterParaDTO).toList());
        return dto;
    }

    /* =========================
       CONVERTER DTO
       ========================= */

    private RelatorioResponseDTO converterParaDTO(Relatorio relatorio) {
        RelatorioResponseDTO dto = new RelatorioResponseDTO();
        dto.setId(relatorio.getId());
        dto.setCelulaId(relatorio.getCelula().getId());
        dto.setNomeCelula(relatorio.getCelula().getNome());
        dto.setNomeLider(
                relatorio.getCelula().getLider() != null
                        ? relatorio.getCelula().getLider().getNome()
                        : "Sem líder"
        );
        dto.setDataReuniao(relatorio.getDataReuniao());
        dto.setEstudo(relatorio.getEstudo());

        if (relatorio.getPresentes() != null) {
            dto.setMembrosPresentes(
                    relatorio.getPresentes().stream()
                            .map(m -> new PessoaPresencaDTO(m.getId(), m.getNome(), DecisaoEspiritual.NENHUMA))
                            .toList()
            );
        }

        if (relatorio.getVisitantesPresentes() != null) {
            dto.setVisitantesPresentes(
                    relatorio.getVisitantesPresentes().stream()
                            .map(v -> new PessoaPresencaDTO(
                                    v.getId(),
                                    v.getNome(),
                                    v.getDecisaoEspiritual() != null
                                            ? v.getDecisaoEspiritual()
                                            : DecisaoEspiritual.NENHUMA
                            ))
                            .toList()
            );
        }

        // Buscar membros ausentes com justificativa
        if (relatorio.getId() != null) {
            List<Presenca> ausencias = presencaRepository.findByRelatorioIdAndPresenteFalse(relatorio.getId());
            if (!ausencias.isEmpty()) {
                dto.setMembrosAusentes(
                        ausencias.stream()
                                .map(p -> new PessoaPresencaDTO(
                                        p.getMembro().getId(),
                                        p.getMembro().getNome(),
                                        DecisaoEspiritual.NENHUMA,
                                        p.getJustificativaFalta()
                                ))
                                .toList()
                );
            }
        }

        int visitantesAvulsos = relatorio.getQuantidadeVisitantes() != null
                ? relatorio.getQuantidadeVisitantes() : 0;
        dto.setQuantidadeVisitantes(visitantesAvulsos);
        dto.setTotalPresentes(relatorio.getTotalPresentes());
        dto.setRealizada(relatorio.getRealizada());
        if (relatorio.getMotivoNaoRealizacao() != null) {
            dto.setMotivoNaoRealizacao(relatorio.getMotivoNaoRealizacao().name());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> buscarPorSemana(String data) {
        LocalDate dataBase = LocalDate.parse(data);
        LocalDate inicioSemana = dataBase.with(DayOfWeek.SUNDAY);
        LocalDate fimSemana    = inicioSemana.plusDays(6);

        return relatorioRepository
                .findByDataReuniaoBetween(inicioSemana, fimSemana)
                .stream()
                .sorted(Comparator.comparing(Relatorio::getDataReuniao).reversed())
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RelatorioDiscipuladoDTO> listarTodosOsRelatorios() {
        List<DiscipuladoRelatorio> todos = discipuladoRelatorioRepository.findAllWithEagerRelationships();

        return todos.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getLider().getId() + "-" + r.getSemanaInicio()
                ))
                .values().stream()
                .map(listaDoGrupo -> {
                    DiscipuladoRelatorio primeiro = listaDoGrupo.get(0);
                    Usuario lider = primeiro.getLider();

                    Celula celulaDoRelatorio = primeiro.getCelula();

                    Long celulaId = null;
                    String nomeCelula = "Célula não informada";

                    if (celulaDoRelatorio != null) {
                        celulaId = celulaDoRelatorio.getId();
                        nomeCelula = celulaDoRelatorio.getNome();
                    } else {
                        if (lider != null && lider.getCelula() != null) {
                            celulaId = lider.getCelula().getId();
                            nomeCelula = lider.getCelula().getNome();
                        }
                    }

                    List<PresencaMembroDTO> presencas = listaDoGrupo.stream()
                            .map(r -> new PresencaMembroDTO(
                                    r.getId(),
                                    r.getMembro().getNome(),
                                    safe(r.isEscolaBiblica()),
                                    safe(r.isQuartaNoite()),
                                    safe(r.isQuintaNoite()),
                                    safe(r.isDomingoManha()),
                                    safe(r.isDomingoNoite()),
                                    r.getJustEscolaBiblica(),
                                    r.getJustQuartaNoite(),
                                    r.getJustQuintaNoite(),
                                    r.getJustDomingoManha(),
                                    r.getJustDomingoNoite()
                            ))
                            .collect(Collectors.toList());

                    return new RelatorioDiscipuladoDTO(
                            primeiro.getId(),
                            celulaId,
                            nomeCelula,
                            lider != null ? lider.getNome() : "Líder desconhecido",
                            primeiro.getSemanaInicio(),
                            primeiro.getSemanaFim(),
                            presencas
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void atualizarRelatorio(Long id, RelatorioRequestDTO dto) throws AccessDeniedException {
        Usuario lider = usuarioService.getUsuarioLogado();

        Celula celula = celulaRepository.findByLider_Id(lider.getId())
                .orElseThrow(() -> new RuntimeException("Líder não possui célula vinculada"));

        Relatorio relatorio = relatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));

        if (!relatorio.getCelula().getId().equals(celula.getId())) {
            throw new AccessDeniedException("Você não tem permissão para editar este relatório");
        }

        relatorio.setDataReuniao(dto.getDataReuniao());
        relatorio.setEstudo(dto.getEstudo());
        relatorio.setQuantidadeVisitantes(
                dto.getQuantidadeVisitantes() != null ? dto.getQuantidadeVisitantes() : 0
        );

        if (dto.getMembrosPresentesIds() != null) {
            List<Membro> membros = membroRepository.findAllById(dto.getMembrosPresentesIds());
            relatorio.setPresentes(membros);
        }

        if (dto.getVisitantesPresentes() != null) {
            List<Long> ids = dto.getVisitantesPresentes().stream()
                    .map(v -> v.getId()).toList();
            List<Visitante> visitantes = visitanteRepository.findAllById(ids);

            for (Visitante visitante : visitantes) {
                dto.getVisitantesPresentes().stream()
                        .filter(v -> v.getId().equals(visitante.getId()))
                        .findFirst()
                        .ifPresent(vdto -> {
                            visitante.setDecisaoEspiritual(
                                    vdto.getDecisaoEspiritual() != null
                                            ? vdto.getDecisaoEspiritual()
                                            : DecisaoEspiritual.NENHUMA
                            );
                        });
            }
            visitanteRepository.saveAll(visitantes);
            visitanteRepository.flush();
            relatorio.setVisitantesPresentes(visitantes);
        }

        relatorioRepository.save(relatorio);

        // Atualizar ausentes: remove os anteriores e salva os novos
        presencaRepository.deleteAll(presencaRepository.findByRelatorioId(relatorio.getId()));
        salvarMembrosAusentes(dto, relatorio);
    }

    @Transactional(readOnly = true)
    public RelatorioResponseDTO buscarPorId(Long id) {
        Relatorio relatorio = relatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));
        return converterParaDTO(relatorio);
    }

    public Relatorio salvar(Relatorio relatorio) {
        Relatorio salvo = relatorioRepository.save(relatorio);
        rankingCelulaService.limparCache();
        return salvo;
    }

    public void deletar(Long id) {
        relatorioRepository.deleteById(id);
        rankingCelulaService.limparCache();
    }

    @Transactional
    public RelatorioNaoRealizadaResponse registrarNaoRealizada(
            RelatorioNaoRealizadaRequest request,
            String username) {

        Celula celula = celulaRepository.findById(request.getCelulaId())
                .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada"));

        boolean jaExiste = relatorioRepository
                .existsByCelulaIdAndDataReuniao(request.getCelulaId(), request.getDataReuniao());
        if (jaExiste) {
            throw new IllegalStateException("Já existe um relatório para esta data.");
        }

        Relatorio relatorio = new Relatorio();
        relatorio.setCelula(celula);
        relatorio.setDataReuniao(request.getDataReuniao());
        relatorio.setRealizada(false);
        relatorio.setMotivoNaoRealizacao(request.getMotivoNaoRealizacao());

        Relatorio salvo = relatorioRepository.save(relatorio);

        RelatorioNaoRealizadaResponse response = new RelatorioNaoRealizadaResponse();
        response.setId(salvo.getId());
        response.setCelulaId(celula.getId());
        response.setNomeCelula(celula.getNome());
        response.setDataReuniao(salvo.getDataReuniao());
        response.setRealizada(false);
        response.setMotivoNaoRealizacao(salvo.getMotivoNaoRealizacao());
        response.setCriadoEm(salvo.getDataCadastro());

        return response;
    }

    /* =========================
       MÉTODO AUXILIAR - AUSENTES
       ========================= */

    private void salvarMembrosAusentes(RelatorioRequestDTO dto, Relatorio relatorio) {
        if (dto.getMembrosAusentes() == null || dto.getMembrosAusentes().isEmpty()) return;

        List<Presenca> ausencias = dto.getMembrosAusentes().stream()
                .map(ausenteDTO -> {
                    Membro membro = membroRepository.findById(ausenteDTO.getMembroId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Membro não encontrado: " + ausenteDTO.getMembroId()));
                    Presenca p = new Presenca();
                    p.setMembro(membro);
                    p.setData(relatorio.getDataReuniao());
                    p.setPresente(false);
                    p.setTipoEvento("CELULA");
                    p.setRelatorio(relatorio);
                    p.setJustificativaFalta(ausenteDTO.getJustificativa());
                    return p;
                })
                .toList();

        presencaRepository.saveAll(ausencias);
    }
}