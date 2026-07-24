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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscipuladoRelatorioService {

    private static final Logger log = LoggerFactory.getLogger(DiscipuladoRelatorioService.class);

    private static final String CELULA_NAO_INFORMADA = "CELULA_NAO_INFORMADA";
    private static final String LIDER_DESCONHECIDO  = "LIDER_DESCONHECIDO";

    private final DiscipuladoRelatorioRepository repository;
    private final MembroRepository               membroRepository;
    private final UsuarioRepository              usuarioRepository;
    private final CelulaRepository               celulaRepository;

    // ── helpers ─────────────────────────────────────────────────────────────

    private boolean safe(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    private int contarPresencas(DiscipuladoRelatorio r) {
        int count = 0;
        if (safe(r.isEscolaBiblica())) count++;
        if (safe(r.isQuartaNoite()))   count++;
        if (safe(r.isQuintaNoite()))   count++;
        if (safe(r.isDomingoManha()))  count++;
        if (safe(r.isDomingoNoite()))  count++;
        return count;
    }

    private String loggedUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @Cacheable(value = "usuario-logado", key = "#email")
    private Usuario usuarioLogado(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException(
                        "Usuário autenticado não encontrado: " + email));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SALVAR (criar ou atualizar) relatório semanal
    // ════════════════════════════════════════════════════════════════════════
    @Transactional
    public void salvarRelatorioSemanal(List<DiscipuladoRequestDTO> lista,
                                       LocalDate inicio,
                                       LocalDate fim) {
        Usuario lider = usuarioLogado(loggedUserEmail());

        List<Long> membroIds = lista.stream()
                .map(DiscipuladoRequestDTO::membroId)
                .collect(Collectors.toList());

        List<DiscipuladoRelatorio> existentes = repository
                .findByMembroIdInAndSemanaInicioAndSemanaFim(membroIds, inicio, fim);

        Map<Long, DiscipuladoRelatorio> existentesMap = existentes.stream()
                .collect(Collectors.toMap(r -> r.getMembro().getId(), Function.identity()));

        List<Long> novosMembroIds = lista.stream()
                .map(DiscipuladoRequestDTO::membroId)
                .filter(id -> !existentesMap.containsKey(id))
                .collect(Collectors.toList());

        Map<Long, Celula> celulaCache = new HashMap<>();

        Map<Long, Membro> novosMembros = membroRepository.findAllById(novosMembroIds).stream()
                .collect(Collectors.toMap(Membro::getId, Function.identity()));

        List<DiscipuladoRelatorio> paraSalvar = new ArrayList<>();

        for (DiscipuladoRequestDTO dto : lista) {
            DiscipuladoRelatorio existente = existentesMap.get(dto.membroId());
            if (existente != null) {
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
                continue;
            }

            Membro membro = novosMembros.get(dto.membroId());
            if (membro == null) {
                throw new jakarta.persistence.EntityNotFoundException(
                        "Membro não encontrado com ID: " + dto.membroId());
            }

            Celula celula;
            if (dto.celulaId() != null) {
                celula = celulaCache.computeIfAbsent(dto.celulaId(), id ->
                        celulaRepository.findById(id).orElseThrow(() ->
                                new RuntimeException("Célula não encontrada: " + id)));
            } else {
                celula = membro.getCelula();
            }

            DiscipuladoRelatorio relatorio = new DiscipuladoRelatorio();
            relatorio.setSemanaInicio(inicio);
            relatorio.setSemanaFim(fim);
            relatorio.setMembro(membro);
            relatorio.setCelula(celula);
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

        repository.saveAll(paraSalvar);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LISTAR semana específica
    // ════════════════════════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public List<RelatorioDiscipuladoDTO> listarSemana(LocalDate inicio, LocalDate fim) {
        Usuario lider  = usuarioLogado(loggedUserEmail());
        Celula  celula = lider.getCelula();
        if (celula == null) return List.of();

        return repository
                .findBySemanaInicioAndSemanaFimAndCelulaId(inicio, fim, celula.getId())
                .stream()
                .map(r -> {
                    List<PresencaMembroDTO> presencas = List.of(new PresencaMembroDTO(
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
                    ));
                    Celula  cel = r.getCelula();
                    Usuario lid = r.getLider();
                    return new RelatorioDiscipuladoDTO(
                            r.getId(),
                            cel != null ? cel.getId()   : null,
                            cel != null ? cel.getNome() : CELULA_NAO_INFORMADA,
                            lid != null ? lid.getNome() : LIDER_DESCONHECIDO,
                            r.getSemanaInicio(),
                            r.getSemanaFim(),
                            presencas
                    );
                })
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LISTAR todos (painel admin) — com filtro de data padrão
    // ════════════════════════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public Page<RelatorioDiscipuladoDTO> listarTodosOsRelatorios(
            LocalDate inicio, LocalDate fim, Pageable pageable) {

        if (inicio == null) inicio = LocalDate.now().minusMonths(6);
        if (fim    == null) fim    = LocalDate.now();

        Page<DiscipuladoRelatorio> page =
                repository.findBySemanaInicioBetween(inicio, fim, pageable);

        List<RelatorioDiscipuladoDTO> dtos = page.getContent()
                .stream()
                .collect(Collectors.groupingBy(
                        // ← null-safe: lider pode ser null
                        r -> {
                            Long liderId = r.getLider() != null
                                    ? r.getLider().getId() : 0L;
                            return liderId + "-" + r.getSemanaInicio();
                        }
                ))
                .values().stream()
                .map(grupo -> {
                    DiscipuladoRelatorio primeiro = grupo.get(0);
                    Usuario lider  = primeiro.getLider();
                    Celula  celula = primeiro.getCelula();

                    Long   celulaId   = null;
                    String nomeCelula = CELULA_NAO_INFORMADA;

                    if (celula != null) {
                        celulaId   = celula.getId();
                        nomeCelula = celula.getNome();
                    } else if (lider != null && lider.getCelula() != null) {
                        celulaId   = lider.getCelula().getId();
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
                            .collect(Collectors.toList());

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
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }
    // ════════════════════════════════════════════════════════════════════════
    //  ALERTAS CRÍTICOS
    // ════════════════════════════════════════════════════════════════════════
    public List<AlertaDTO> obterAlertasCriticos() {
        LocalDate hoje        = LocalDate.now();
        LocalDate inicioSemana = hoje.with(DayOfWeek.MONDAY);
        LocalDate fimSemana   = hoje.with(DayOfWeek.SUNDAY);

        return repository.findBySemanaInicioAndSemanaFim(inicioSemana, fimSemana)
                .stream()
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
                    return new AlertaDTO(
                            r.getMembro().getId(),
                            r.getMembro().getNome(),
                            r.getMembro().getTelefone(),
                            r.getCelula() != null ? r.getCelula().getNome() : "Sem célula",
                            (int) obj[1]
                    );
                })
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ATUALIZAR um único registro (PUT /{id})
    // ════════════════════════════════════════════════════════════════════════
    @Transactional
    public RelatorioDiscipuladoDTO atualizarRelatorio(Long id, DiscipuladoRequestDTO dto) {
        DiscipuladoRelatorio relatorio = repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Relatório não encontrado com ID: " + id));

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
        relatorio.calcularPresenca();
        repository.save(relatorio);

        Celula  celula = relatorio.getCelula();
        Usuario lider  = relatorio.getLider();

        return new RelatorioDiscipuladoDTO(
                relatorio.getId(),
                celula != null ? celula.getId()   : null,
                celula != null ? celula.getNome() : CELULA_NAO_INFORMADA,
                lider  != null ? lider.getNome()  : LIDER_DESCONHECIDO,
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
                        relatorio.getJustEscolaBiblica(),
                        relatorio.getJustQuartaNoite(),
                        relatorio.getJustQuintaNoite(),
                        relatorio.getJustDomingoManha(),
                        relatorio.getJustDomingoNoite()
                ))
        );
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HISTÓRICO PAGINADO
    //
    //  Fluxo:
    //    1. Busca as semanas distintas da célula com paginação no banco
    //       → Page<Object[]> onde Object[0]=semanaInicio, Object[1]=semanaFim
    //    2. Para cada semana da página, faz um fetch dos registros (JOIN FETCH)
    //    3. Agrupa e monta o DiscipuladoHistoricoItemDTO
    //    4. Retorna Page<DiscipuladoHistoricoItemDTO> com os metadados corretos
    // ════════════════════════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public Page<DiscipuladoHistoricoItemDTO> listarHistorico(int page, int size) {

        Usuario lider = usuarioLogado(loggedUserEmail());

        // Célula do líder (com fallback)
        Celula celula = celulaRepository.findByLider_Id(lider.getId())
                .orElse(lider.getCelula());

        log.debug("=== HISTORICO PAGINADO === lider={} celula={}",
                lider.getId(), celula != null ? celula.getId() : "NULL");

        if (celula == null) {
            return Page.empty(PageRequest.of(page, size));
        }

        // 1) Semanas distintas — paginação feita no banco
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> semanasPaginadas =
                repository.findSemanasPaginadas(celula.getId(), pageable);

        log.debug("Semanas na página {}: {}", page, semanasPaginadas.getNumberOfElements());

        final int TOTAL_COLUNAS = 5;
        final Long celulaId = celula.getId();

        // 2) Para cada semana da página, busca os registros e monta o DTO
        List<DiscipuladoHistoricoItemDTO> itens = semanasPaginadas.getContent()
                .stream()
                .map(row -> {
                    LocalDate inicio = (LocalDate) row[0];
                    LocalDate fim    = (LocalDate) row[1];

                    List<DiscipuladoRelatorio> registros =
                            repository.findRegistrosDaSemana(celulaId, inicio, fim);

                    // Pega o primeiro para usar como referência de ID
                    DiscipuladoRelatorio primeiro = registros.get(0);

                    int totalPresencas = registros.stream()
                            .mapToInt(this::contarPresencas)
                            .sum();

                    int totalPossivel = registros.size() * TOTAL_COLUNAS;
                    int frequencia    = totalPossivel > 0
                            ? (int) Math.round((totalPresencas * 100.0) / totalPossivel)
                            : 0;

                    return DiscipuladoHistoricoItemDTO.builder()
                            .id(primeiro.getId())
                            .inicio(inicio)
                            .fim(fim)
                            .totalMembros(registros.size())
                            .totalPresencas(totalPresencas)
                            .totalPossivel(totalPossivel)
                            .frequencia(frequencia)
                            .build();
                })
                .collect(Collectors.toList());

        // 3) Monta o Page com os metadados corretos (total vem da query de semanas)
        return new PageImpl<>(itens, pageable, semanasPaginadas.getTotalElements());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DETALHE DE UMA SEMANA por ID
    // ════════════════════════════════════════════════════════════════════════
    @Transactional(readOnly = true)
    public DiscipuladoSemanaDetalheDTO buscarDetalhe(Long id) {
        List<DiscipuladoRelatorio> registrosDaSemana =
                repository.findRegistrosDaSemanaPorIdRelatorio(id);

        if (registrosDaSemana.isEmpty()) {
            throw new jakarta.persistence.EntityNotFoundException(
                    "Relatório não encontrado com ID: " + id);
        }

        DiscipuladoRelatorio referencia = registrosDaSemana.get(0);
        Celula celula = referencia.getCelula();

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
                        r.getMembro().getNome(),
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
                .inicio(referencia.getSemanaInicio())
                .fim(referencia.getSemanaFim())
                .membros(membros)
                .presencas(presencas)
                .build();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ATUALIZAR semana completa (PUT /relatorio-semanal/{id})
    // ════════════════════════════════════════════════════════════════════════
    @Transactional
    public void atualizarRelatorioSemanal(Long id,
                                          List<DiscipuladoRequestDTO> lista,
                                          LocalDate inicio,
                                          LocalDate fim) {
        salvarRelatorioSemanal(lista, inicio, fim);
    }
}