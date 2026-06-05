package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.*;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import com.gestaoigrejaemcelula.demo.domain.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

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

    // ✅ Helper para evitar NullPointerException em campos Boolean do banco
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

        // Tenta primeiro como líder
        Optional<Celula> celulaComoLider = celulaRepository.findByLider_Id(usuario.getId());

        if (celulaComoLider.isPresent()) {
            celula = celulaComoLider.get();
        } else if (usuario.getCelula() != null) {
            // Secretário ou membro com célula vinculada diretamente
            celula = usuario.getCelula();
        } else {
            return List.of(); // sem célula, retorna vazio sem lançar exceção
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
        List<Relatorio> relatorios = relatorioRepository.findByDataReuniaoBetween(inicio, fim);

        int totalMembros = relatorios.stream()
                .mapToInt(r -> r.getQuantidadeMembros())
                .sum();

        int totalVisitantes = relatorios.stream()
                .mapToInt(r -> r.getTotalVisitantes())
                .sum();

        int totalCelulas = (int) relatorios.stream()
                .map(r -> r.getCelula().getId())
                .distinct()
                .count();

        RelatorioResumoDTO dto = new RelatorioResumoDTO();
        dto.setInicio(inicio);
        dto.setFim(fim);
        dto.setTotalCelulas(totalCelulas);
        dto.setTotalMembros(totalMembros);
        dto.setTotalVisitantes(totalVisitantes);
        dto.setRelatorios(relatorios.stream().map(this::converterParaDTO).toList());

        return dto;
    }

    /* =========================
       CONVERTER DTO FINAL
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
                            .map(v -> {
                                return new PessoaPresencaDTO(
                                        v.getId(),
                                        v.getNome(),
                                        v.getDecisaoEspiritual() != null
                                                ? v.getDecisaoEspiritual()
                                                : DecisaoEspiritual.NENHUMA
                                );
                            })
                            .toList()
            );
        }

        int visitantesAvulsos = relatorio.getQuantidadeVisitantes() != null
                ? relatorio.getQuantidadeVisitantes() : 0;
        dto.setQuantidadeVisitantes(visitantesAvulsos);
        dto.setTotalPresentes(relatorio.getTotalPresentes());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> buscarPorSemana(String data) {
        LocalDate dataBase = LocalDate.parse(data);
        // ✅ domingo a sábado, igual ao frontend
        LocalDate inicioSemana = dataBase.with(DayOfWeek.SUNDAY);
        LocalDate fimSemana    = inicioSemana.plusDays(6); // sábado

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
                                    safe(r.isEscolaBiblica()),  // ✅ seguro contra null
                                    safe(r.isQuartaNoite()),    // ✅ seguro contra null
                                    safe(r.isQuintaNoite()),    // ✅ seguro contra null
                                    safe(r.isDomingoManha()),   // ✅ seguro contra null
                                    safe(r.isDomingoNoite())    // ✅ seguro contra null
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

            // ✅ CORREÇÃO: atualiza a decisão espiritual de cada visitante
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
    }
    @Transactional(readOnly = true)
    public RelatorioResponseDTO buscarPorId(Long id) {
        Relatorio relatorio = relatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));
        return converterParaDTO(relatorio);
    }
    @Autowired
    private RankingCelulaService rankingCelulaService;

    // No método de salvar
    public Relatorio salvar(Relatorio relatorio) {
        Relatorio salvo = relatorioRepository.save(relatorio);
        rankingCelulaService.limparCache(); // limpa o cache
        return salvo;
    }

    // No método de deletar
    public void deletar(Long id) {
        relatorioRepository.deleteById(id);
        rankingCelulaService.limparCache(); // limpa o cache
    }
}