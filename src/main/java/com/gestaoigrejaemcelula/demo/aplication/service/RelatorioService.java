package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.*;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired private PresencaRepository presencaRepository;
    @Autowired private RelatorioRepository relatorioRepository;
    @Autowired private CelulaRepository celulaRepository;
    @Autowired private MembroRepository membroRepository;
    @Autowired private VisitanteRepository visitanteRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private UsuarioService usuarioService;
    @Autowired private DiscipuladoRelatorioRepository discipuladoRelatorioRepository;
    @Autowired private RankingCelulaService rankingCelulaService;
    @Autowired private RelatorioMensalService relatorioMensalService;

    private static final String CELULA_NAO_INFORMADA = "CELULA_NAO_INFORMADA";
    private static final String LIDER_DESCONHECIDO  =" LIDER_DESCONHECIDO";

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

        // ✅ CORREÇÃO: Bloqueia relatório duplicado para a mesma célula e data
        if (relatorioRepository.existsByCelulaIdAndDataReuniao(celula.getId(), dto.getDataReuniao())) {
            throw new IllegalStateException("Já existe um relatório para esta célula nesta data. Não é permitido enviar mais de um relatório por reunião.");
        }

        Relatorio relatorio = new Relatorio();
        relatorio.setCelula(celula);
        relatorio.setDataReuniao(dto.getDataReuniao());
        relatorio.setEstudo(dto.getEstudo());
        relatorio.setQuantidadeVisitantes(dto.getQuantidadeVisitantes() != null ? dto.getQuantidadeVisitantes() : 0);

        if (dto.getMembrosPresentesIds() != null && !dto.getMembrosPresentesIds().isEmpty()) {
            List<Membro> membros = membroRepository.findAllById(dto.getMembrosPresentesIds());
            if (membros.size() != dto.getMembrosPresentesIds().size()) {
                throw new IllegalArgumentException("Um ou mais membros não existem");
            }
            relatorio.setPresentes(membros);
        }

        if (dto.getVisitantesPresentes() != null && !dto.getVisitantesPresentes().isEmpty()) {
            relatorio.setVisitantesPresentes(new HashSet<>(resolverVisitantes(dto)));
        }

        relatorioRepository.save(relatorio);
        salvarMembrosAusentes(dto, relatorio);

        relatorioMensalService.verificarEEnviarParabens(celula.getId());
    }

    /* =========================
       LISTAGENS
       ========================= */

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> listarRelatoriosUltimosSeteDias() {
        LocalDate seteDiasAtras = LocalDate.now(ZoneId.of("America/Sao_Paulo")).minusDays(7);
        return relatorioRepository
                .findByDataReuniaoGreaterThanEqual(seteDiasAtras)
                .stream()
                .sorted(Comparator.comparing(Relatorio::getDataReuniao).reversed())
                .map(this::converterParaDTO)
                .toList();
    }

    /**
     * Listagem paginada — use ?page=0&size=20 no controller.
     * Evita carregar todos os registros na memória de uma vez.
     */
    @Transactional(readOnly = true)
    public Page<RelatorioResponseDTO> listarTodosComoDTO(Pageable pageable) {
        return relatorioRepository.findAll(pageable).map(this::converterParaDTO);
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

        Optional<Celula> celulaComoLider = celulaRepository.findByLider_Id(usuario.getId());

        Celula celula;
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
        List<Relatorio> todos = relatorioRepository.findRelatoriosEntreDatasComCelula(inicio, fim);

        List<Relatorio> realizadas = todos.stream()
                .filter(r -> r.getRealizada() == null || r.getRealizada())
                .toList();

        int totalMembros    = realizadas.stream().mapToInt(Relatorio::getQuantidadeMembros).sum();
        int totalVisitantes = realizadas.stream().mapToInt(Relatorio::getTotalVisitantes).sum();
        int totalCelulas    = (int) realizadas.stream().map(r -> r.getCelula().getId()).distinct().count();

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
        dto.setNomeLider(relatorio.getCelula().getLider() != null
                ? relatorio.getCelula().getLider().getNome()
                : "Sem líder");
        dto.setDataReuniao(relatorio.getDataReuniao());
        dto.setEstudo(relatorio.getEstudo());

        if (relatorio.getPresentes() != null) {
            dto.setMembrosPresentes(relatorio.getPresentes().stream()
                    .map(m -> new PessoaPresencaDTO(m.getId(), m.getNome(), DecisaoEspiritual.NENHUMA))
                    .toList());
        }

        if (relatorio.getVisitantesPresentes() != null) {
            dto.setVisitantesPresentes(relatorio.getVisitantesPresentes().stream()
                    .map(v -> new PessoaPresencaDTO(
                            v.getId(),
                            v.getNome(),
                            v.getDecisaoEspiritual() != null ? v.getDecisaoEspiritual() : DecisaoEspiritual.NENHUMA))
                    .toList());
        }

        if (relatorio.getId() != null) {
            List<Presenca> ausencias = presencaRepository.findByRelatorioIdAndPresenteFalse(relatorio.getId());
            if (!ausencias.isEmpty()) {
                dto.setMembrosAusentes(ausencias.stream()
                        .map(p -> new PessoaPresencaDTO(
                                p.getMembro().getId(),
                                p.getMembro().getNome(),
                                DecisaoEspiritual.NENHUMA,
                                p.getJustificativaFalta()))
                        .toList());
            }
        }

        dto.setQuantidadeVisitantes(relatorio.getQuantidadeVisitantes() != null ? relatorio.getQuantidadeVisitantes() : 0);
        dto.setTotalPresentes(relatorio.getTotalPresentes());
        dto.setRealizada(relatorio.getRealizada());
        if (relatorio.getMotivoNaoRealizacao() != null) {
            dto.setMotivoNaoRealizacao(relatorio.getMotivoNaoRealizacao().name());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> buscarPorSemana(String data) {
        LocalDate dataBase     = LocalDate.parse(data);
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

        Map<String, List<DiscipuladoRelatorio>> agrupado = todos.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getLider().getId() + "-" + r.getSemanaInicio()
                ));

        return agrupado.values().stream()
                .map(grupo -> {
                    DiscipuladoRelatorio primeiro = grupo.get(0);
                    Usuario lider = primeiro.getLider();
                    Celula celulaDoRelatorio = primeiro.getCelula();

                    Long celulaId = null;
                    String nomeCelula = CELULA_NAO_INFORMADA;

                    if (celulaDoRelatorio != null) {
                        celulaId  = celulaDoRelatorio.getId();
                        nomeCelula = celulaDoRelatorio.getNome();
                    } else if (lider != null && lider.getCelula() != null) {
                        celulaId  = lider.getCelula().getId();
                        nomeCelula = lider.getCelula().getNome();
                    }

                    List<PresencaMembroDTO> presencas = grupo.stream()
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
                            .toList();

                    return new RelatorioDiscipuladoDTO(
                            primeiro.getId(),
                            celulaId,
                            nomeCelula,
                            lider != null ? lider.getNome() : LIDER_DESCONHECIDO,
                            primeiro.getSemanaInicio(),
                            primeiro.getSemanaFim(),
                            presencas
                    );
                })
                .toList();
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
        relatorio.setQuantidadeVisitantes(dto.getQuantidadeVisitantes() != null ? dto.getQuantidadeVisitantes() : 0);

        if (dto.getMembrosPresentesIds() != null) {
            relatorio.setPresentes(membroRepository.findAllById(dto.getMembrosPresentesIds()));
        }

        if (dto.getVisitantesPresentes() != null) {
            relatorio.setVisitantesPresentes(new HashSet<>(resolverVisitantes(dto)));
        }

        relatorioRepository.save(relatorio);

        presencaRepository.deleteAll(presencaRepository.findByRelatorioId(relatorio.getId()));
        salvarMembrosAusentes(dto, relatorio);

        relatorioMensalService.verificarEEnviarParabens(celula.getId());
    }

    @Transactional(readOnly = true)
    public RelatorioResponseDTO buscarPorId(Long id) {
        Relatorio relatorio = relatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));
        return converterParaDTO(relatorio);
    }

    @Transactional
    public Relatorio salvar(Relatorio relatorio) {
        Relatorio salvo = relatorioRepository.save(relatorio);
        rankingCelulaService.limparCache();
        return salvo;
    }

    @Transactional
    public void deletar(Long id) {
        relatorioRepository.deleteById(id);
        rankingCelulaService.limparCache();
    }

    @Transactional
    public RelatorioNaoRealizadaResponse registrarNaoRealizada(RelatorioNaoRealizadaRequest request, String username) {
        Celula celula = celulaRepository.findById(request.getCelulaId())
                .orElseThrow(() -> new EntityNotFoundException("Célula não encontrada"));

        if (relatorioRepository.existsByCelulaIdAndDataReuniao(request.getCelulaId(), request.getDataReuniao())) {
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
       MÉTODOS AUXILIARES
       ========================= */

    private List<Visitante> resolverVisitantes(RelatorioRequestDTO dto) {
        List<Long> ids = dto.getVisitantesPresentes().stream()
                .map(VisitantePresencaDTO::getId)
                .toList();

        Map<Long, DecisaoEspiritual> decisoes = dto.getVisitantesPresentes().stream()
                .collect(Collectors.toMap(
                        VisitantePresencaDTO::getId,
                        v -> v.getDecisaoEspiritual() != null
                                ? v.getDecisaoEspiritual()
                                : DecisaoEspiritual.NENHUMA
                ));

        List<Visitante> visitantes = visitanteRepository.findAllById(ids);
        visitantes.forEach(v -> v.setDecisaoEspiritual(
                decisoes.getOrDefault(v.getId(), DecisaoEspiritual.NENHUMA)));

        visitanteRepository.saveAll(visitantes);
        visitanteRepository.flush();
        return visitantes;
    }

    private void salvarMembrosAusentes(RelatorioRequestDTO dto, Relatorio relatorio) {
        if (dto.getMembrosAusentes() == null || dto.getMembrosAusentes().isEmpty()) return;

        List<Presenca> ausencias = dto.getMembrosAusentes().stream()
                .map(ausenteDTO -> {
                    Membro membro = membroRepository.findById(ausenteDTO.getMembroId())
                            .orElseThrow(() -> new RuntimeException("Membro não encontrado: " + ausenteDTO.getMembroId()));
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