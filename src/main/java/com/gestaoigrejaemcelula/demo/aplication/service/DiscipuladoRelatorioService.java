package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoRelatorio;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoRelatorioRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscipuladoRelatorioService {

    private static final Logger log = LoggerFactory.getLogger(DiscipuladoRelatorioService.class);

    private final DiscipuladoRelatorioRepository repository;
    private final MembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository;
    private final CelulaRepository celulaRepository;

    // ─── Helper seguro para Boolean nullable ────────────────────────────────
    private boolean safe(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    // ─── Usuário autenticado ─────────────────────────────────────────────────
    private Usuario usuarioLogado() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado: " + email));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SALVAR (criar ou atualizar) relatório semanal
    // ════════════════════════════════════════════════════════════════════════
    @Transactional
    public void salvarRelatorioSemanal(List<DiscipuladoRequestDTO> lista,
                                       LocalDate inicio,
                                       LocalDate fim) {
        Usuario lider = usuarioLogado();
        List<DiscipuladoRelatorio> paraSalvar = new ArrayList<>();

        for (DiscipuladoRequestDTO dto : lista) {
            repository.findByMembroIdAndSemanaInicioAndSemanaFim(dto.membroId(), inicio, fim)
                    .ifPresentOrElse(
                            existente -> {
                                existente.setEscolaBiblica(dto.escolaBiblica());
                                existente.setQuartaNoite(dto.quartaNoite());
                                existente.setQuintaNoite(dto.quintaNoite());
                                existente.setDomingoManha(dto.domingoManha());
                                existente.setDomingoNoite(dto.domingoNoite());

                                existente.setJustEscolaBiblica(dto.justEscolaBiblica());
                                existente.setJustQuartaNoite(dto.justQuartaNoite());
                                existente.setJustQuintaNoite(dto.justQuintaNoite());
                                existente.setJustDomingoManha(dto.justDomingoManha());
                                existente.setJustDomingoNoite(dto.justDomingoNoite());
                                existente.calcularPresenca();
                                paraSalvar.add(existente);
                            },
                            () -> {
                                Membro membro = membroRepository.findById(dto.membroId())
                                        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                                                "Membro não encontrado com ID: " + dto.membroId()));

                                DiscipuladoRelatorio relatorio = new DiscipuladoRelatorio();
                                relatorio.setSemanaInicio(inicio);
                                relatorio.setSemanaFim(fim);
                                relatorio.setMembro(membro);
                                relatorio.setCelula(dto.celulaId() != null
                                        ? celulaRepository.findById(dto.celulaId())
                                        .orElseThrow(() -> new RuntimeException("Célula não encontrada: " + dto.celulaId()))
                                        : membro.getCelula());
                                relatorio.setEscolaBiblica(dto.escolaBiblica());
                                relatorio.setQuartaNoite(dto.quartaNoite());
                                relatorio.setQuintaNoite(dto.quintaNoite());
                                relatorio.setDomingoManha(dto.domingoManha());
                                relatorio.setDomingoNoite(dto.domingoNoite());
                                relatorio.setJustEscolaBiblica(dto.justEscolaBiblica());
                                relatorio.setJustQuartaNoite(dto.justQuartaNoite());
                                relatorio.setJustQuintaNoite(dto.justQuintaNoite());
                                relatorio.setJustDomingoManha(dto.justDomingoManha());
                                relatorio.setJustDomingoNoite(dto.justDomingoNoite());
                                relatorio.setLider(lider);
                                relatorio.setDataEnvio(LocalDateTime.now());
                                relatorio.calcularPresenca();
                                paraSalvar.add(relatorio);
                            }
                    );
        }

        repository.saveAll(paraSalvar);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LISTAR semana específica
    // ════════════════════════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public List<RelatorioDiscipuladoDTO> listarSemana(LocalDate inicio, LocalDate fim) {
        Usuario lider = usuarioLogado();
        Celula celula = lider.getCelula();
        if (celula == null) return List.of();

        List<DiscipuladoRelatorio> lista =
                repository.findBySemanaInicioAndSemanaFimAndCelulaId(inicio, fim, celula.getId());

        return lista.stream().map(r -> {
            List<PresencaMembroDTO> presencas = List.of(new PresencaMembroDTO(
                    r.getId(),
                    r.getMembro().getNome(),
                    safe(r.isEscolaBiblica()),
                    safe(r.isQuartaNoite()),
                    safe(r.isQuintaNoite()),
                    safe(r.isDomingoManha()),
                    safe(r.isDomingoNoite()),
                    // ✅ ADICIONE ESSAS 5 LINHAS:
                    r.getJustEscolaBiblica(),
                    r.getJustQuartaNoite(),
                    r.getJustQuintaNoite(),
                    r.getJustDomingoManha(),
                    r.getJustDomingoNoite()
            ));
            Celula cel = r.getCelula();
            Usuario lid = r.getLider();
            return new RelatorioDiscipuladoDTO(
                    r.getId(),
                    cel != null ? cel.getId() : null,
                    cel != null ? cel.getNome() : "Célula não informada",
                    lid != null ? lid.getNome() : "Líder desconhecido",
                    r.getSemanaInicio(),
                    r.getSemanaFim(),
                    presencas
            );
        }).collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LISTAR todos (painel admin)
    // ════════════════════════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public List<RelatorioDiscipuladoDTO> listarTodosOsRelatorios() {
        List<DiscipuladoRelatorio> todos = repository.findAllWithEagerRelationships();

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
                    } else if (lider != null && lider.getCelula() != null) {
                        celulaId = lider.getCelula().getId();
                        nomeCelula = lider.getCelula().getNome();
                    }
// ✅ CÓDIGO CORRIGIDO - Para o método listarTodosOsRelatorios()

                    List<PresencaMembroDTO> presencas = listaDoGrupo.stream()
                            .map(r -> new PresencaMembroDTO(
                                    r.getId(),
                                    r.getMembro().getNome(),
                                    safe(r.isEscolaBiblica()),
                                    safe(r.isQuartaNoite()),
                                    safe(r.isQuintaNoite()),
                                    safe(r.isDomingoManha()),
                                    safe(r.isDomingoNoite()),
                                    // ✅ ADICIONADAS ESSAS 5 LINHAS:
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

    // ════════════════════════════════════════════════════════════════════════
    //  ALERTAS CRÍTICOS
    // ════════════════════════════════════════════════════════════════════════
    public List<AlertaDTO> obterAlertasCriticos() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(DayOfWeek.MONDAY);
        LocalDate fimSemana    = hoje.with(DayOfWeek.SUNDAY);

        List<DiscipuladoRelatorio> relatorios =
                repository.findBySemanaInicioAndSemanaFim(inicioSemana, fimSemana);

        return relatorios.stream()
                .map(r -> {
                    int faltas = 0;
                    if (!safe(r.isEscolaBiblica())) faltas++;
                    if (!safe(r.isQuartaNoite()))   faltas++;
                    if (!safe(r.isQuintaNoite()))   faltas++;
                    if (!safe(r.isDomingoManha()))  faltas++;
                    if (!safe(r.isDomingoNoite()))  faltas++;
                    return new Object[]{r, faltas};
                })
                .filter(obj -> (int) obj[1] >= 2)
                .map(obj -> {
                    DiscipuladoRelatorio r = (DiscipuladoRelatorio) obj[0];
                    int faltas = (int) obj[1];
                    return new AlertaDTO(
                            r.getMembro().getId(),
                            r.getMembro().getNome(),
                            r.getMembro().getTelefone(),
                            r.getCelula() != null ? r.getCelula().getNome() : "Sem célula",
                            faltas
                    );
                })
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ATUALIZAR um único registro (endpoint legado PUT /{id})
    // ════════════════════════════════════════════════════════════════════════
    // ✅ CÓDIGO CORRIGIDO - Método atualizarRelatorio()

    @Transactional
    public RelatorioDiscipuladoDTO atualizarRelatorio(Long id, DiscipuladoRequestDTO dto) {
        DiscipuladoRelatorio relatorio = repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Relatório não encontrado com ID: " + id));

        // Presença
        relatorio.setEscolaBiblica(dto.escolaBiblica());
        relatorio.setQuartaNoite(dto.quartaNoite());
        relatorio.setQuintaNoite(dto.quintaNoite());
        relatorio.setDomingoManha(dto.domingoManha());
        relatorio.setDomingoNoite(dto.domingoNoite());

        // ✅ ADICIONADAS: Justificativas
        relatorio.setJustEscolaBiblica(dto.justEscolaBiblica());
        relatorio.setJustQuartaNoite(dto.justQuartaNoite());
        relatorio.setJustQuintaNoite(dto.justQuintaNoite());
        relatorio.setJustDomingoManha(dto.justDomingoManha());
        relatorio.setJustDomingoNoite(dto.justDomingoNoite());

        relatorio.calcularPresenca();
        repository.save(relatorio);

        Celula celula = relatorio.getCelula();
        Usuario lider = relatorio.getLider();

        return new RelatorioDiscipuladoDTO(
                relatorio.getId(),
                celula != null ? celula.getId() : null,
                celula != null ? celula.getNome() : "Célula não informada",
                lider != null ? lider.getNome() : "Líder desconhecido",
                relatorio.getSemanaInicio(),
                relatorio.getSemanaFim(),
                List.of(new PresencaMembroDTO(
                        relatorio.getId(),
                        relatorio.getMembro().getNome(),
                        safe(relatorio.isEscolaBiblica()),
                        safe(relatorio.isQuartaNoite()),
                        safe(relatorio.isQuintaNoite()),
                        safe(relatorio.isDomingoManha()),
                        safe(relatorio.isDomingoNoite()),
                        // ✅ ADICIONADAS: Justificativas no retorno
                        relatorio.getJustEscolaBiblica(),
                        relatorio.getJustQuartaNoite(),
                        relatorio.getJustQuintaNoite(),
                        relatorio.getJustDomingoManha(),
                        relatorio.getJustDomingoNoite()
                ))
        );
    }
    // ════════════════════════════════════════════════════════════════════════
    //  HISTÓRICO — lista resumida de semanas da célula do líder logado
    //
    //  Modelo real: cada DiscipuladoRelatorio = 1 membro × 1 semana.
    //  Agrupa por (semanaInicio, semanaFim) para montar um item por semana.
    // ════════════════════════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public List<DiscipuladoHistoricoItemDTO> listarHistorico() {
        Usuario lider = usuarioLogado();

        // Busca célula onde o usuário é líder, com fallback para celula_id do usuário
        Celula celula = celulaRepository.findByLider_Id(lider.getId())
                .orElse(lider.getCelula());

        log.debug("=== HISTORICO DEBUG ===");
        log.debug("Lider ID: {} | Email: {}", lider.getId(), lider.getEmail());
        log.debug("Celula: {}", celula != null ? celula.getId() + " - " + celula.getNome() : "NULL");

        if (celula == null) return List.of();

        List<DiscipuladoRelatorio> todos =
                repository.findByCelulaIdOrderBySemanaInicioDesc(celula.getId());

        log.debug("Registros encontrados: {}", todos.size());

        Map<String, List<DiscipuladoRelatorio>> porSemana = new LinkedHashMap<>();
        for (DiscipuladoRelatorio r : todos) {
            String chave = r.getSemanaInicio() + "|" + r.getSemanaFim();
            porSemana.computeIfAbsent(chave, k -> new ArrayList<>()).add(r);
        }

        log.debug("Semanas agrupadas: {}", porSemana.size());

        final int TOTAL_COLUNAS = 5;
        return porSemana.values().stream().map(registrosDaSemana -> {
            DiscipuladoRelatorio primeiro = registrosDaSemana.get(0);
            int totalPresencas = registrosDaSemana.stream().mapToInt(r ->
                    (safe(r.isEscolaBiblica()) ? 1 : 0) +
                            (safe(r.isQuartaNoite())   ? 1 : 0) +
                            (safe(r.isQuintaNoite())   ? 1 : 0) +
                            (safe(r.isDomingoManha())  ? 1 : 0) +
                            (safe(r.isDomingoNoite())  ? 1 : 0)
            ).sum();
            int totalPossivel = registrosDaSemana.size() * TOTAL_COLUNAS;
            int frequencia    = totalPossivel > 0
                    ? (int) Math.round((totalPresencas * 100.0) / totalPossivel)
                    : 0;
            return DiscipuladoHistoricoItemDTO.builder()
                    .id(primeiro.getId())
                    .inicio(primeiro.getSemanaInicio())
                    .fim(primeiro.getSemanaFim())
                    .totalMembros(registrosDaSemana.size())
                    .totalPresencas(totalPresencas)
                    .totalPossivel(totalPossivel)
                    .frequencia(frequencia)
                    .build();
        }).collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DETALHE DE UMA SEMANA por ID (tela de edição)
    //
    //  Recebe o ID de qualquer registro da semana, descobre início+fim,
    //  e devolve TODOS os registros daquela semana para aquela célula.
    // ════════════════════════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public DiscipuladoSemanaDetalheDTO buscarDetalhe(Long id) {
        DiscipuladoRelatorio referencia = repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Relatório não encontrado com ID: " + id));

        LocalDate inicio  = referencia.getSemanaInicio();
        LocalDate fim     = referencia.getSemanaFim();
        Celula    celula  = referencia.getCelula();

        // Todos os membros da semana (mesma célula, mesmo período)
        List<DiscipuladoRelatorio> registrosDaSemana =
                repository.findBySemanaInicioAndSemanaFimAndCelulaId(inicio, fim, celula.getId());

        List<DiscipuladoSemanaDetalheDTO.MembroResumoDTO> membros = registrosDaSemana.stream()
                .map(r -> DiscipuladoSemanaDetalheDTO.MembroResumoDTO.builder()
                        .id(r.getMembro().getId())
                        .nome(r.getMembro().getNome())
                        .build())
                .collect(Collectors.toList());

        List<DiscipuladoRequestDTO> presencas = registrosDaSemana.stream()
                .map(r -> new DiscipuladoRequestDTO(
                        r.getMembro().getId(),
                        celula.getId(),
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

        return DiscipuladoSemanaDetalheDTO.builder()
                .id(referencia.getId())
                .nomeCelula(celula.getNome())
                .inicio(inicio)
                .fim(fim)
                .membros(membros)
                .presencas(presencas)
                .build();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ATUALIZAR semana completa (PUT /relatorio-semanal/{id})
    //
    //  Reutiliza salvarRelatorioSemanal que já faz upsert por membro,
    //  então não precisa deletar nada — atualiza quem existe e ignora
    //  membros que saíram (sem registro novo = sem dano).
    // ════════════════════════════════════════════════════════════════════════
    @Transactional
    public void atualizarRelatorioSemanal(Long id,
                                          List<DiscipuladoRequestDTO> lista,
                                          LocalDate inicio,
                                          LocalDate fim) {
        // Valida que o relatório de referência existe
        repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Relatório não encontrado com ID: " + id));

        // salvarRelatorioSemanal já faz upsert (atualiza se existe, cria se não existe)
        salvarRelatorioSemanal(lista, inicio, fim);
    }
}