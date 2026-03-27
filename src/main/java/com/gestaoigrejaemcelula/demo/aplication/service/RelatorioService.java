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
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

        // Visitantes avulsos
        relatorio.setQuantidadeVisitantes(
                dto.getQuantidadeVisitantes() != null ? dto.getQuantidadeVisitantes() : 0
        );

        /* ========= MEMBROS ========= */
        if (dto.getMembrosPresentesIds() != null && !dto.getMembrosPresentesIds().isEmpty()) {

            List<Membro> membros = membroRepository.findAllById(dto.getMembrosPresentesIds());

            if (membros.size() != dto.getMembrosPresentesIds().size()) {
                throw new IllegalArgumentException("Um ou mais membros não existem");
            }

            relatorio.setPresentes(membros);
        }

        /* ========= VISITANTES COM DECISÃO ========= */
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

                            // 🔥 GARANTE SALVAR
                            visitanteRepository.save(visitante);
                        });
            }

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

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Celula celula = celulaRepository.findByLider_Id(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Nenhuma célula vinculada"));

        return relatorioRepository.findByCelulaIdOrderByDataReuniaoDesc(celula.getId())
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    /* =========================
       RESUMO SEMANAL CORRIGIDO
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
        dto.setRelatorios(
                relatorios.stream().map(this::converterParaDTO).toList()
        );

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

        /* MEMBROS */
        if (relatorio.getPresentes() != null) {
            dto.setMembrosPresentes(
                    relatorio.getPresentes().stream()
                            .map(m -> new PessoaPresencaDTO(
                                    m.getId(),
                                    m.getNome(),
                                    DecisaoEspiritual.NENHUMA
                            ))
                            .toList()
            );
        }

        /* VISITANTES */
        if (relatorio.getVisitantesPresentes() != null) {
            dto.setVisitantesPresentes(
                    relatorio.getVisitantesPresentes().stream()
                            .map(v -> new PessoaPresencaDTO(
                                    v.getId(),
                                    v.getNome(),
                                    v.getDecisaoEspiritual()
                            ))
                            .toList()
            );
        }

        int visitantesAvulsos = relatorio.getQuantidadeVisitantes() != null
                ? relatorio.getQuantidadeVisitantes()
                : 0;

        dto.setQuantidadeVisitantes(visitantesAvulsos);

        int total = relatorio.getTotalPresentes();
        dto.setTotalPresentes(total);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> buscarPorSemana(String data) {

        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

        LocalDate dataBase = LocalDate.parse(data);

        LocalDate inicioSemana = dataBase.with(DayOfWeek.MONDAY);
        LocalDate fimSemana = dataBase.with(DayOfWeek.SUNDAY);

        return relatorioRepository
                .findByDataReuniaoBetween(inicioSemana, fimSemana)
                .stream()
                .sorted(Comparator.comparing(Relatorio::getDataReuniao).reversed())
                .map(this::converterParaDTO)
                .toList();
    }

}
