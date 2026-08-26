package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoColetivoRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoColetivoResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoHistoricoItemDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoIndicadoresDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoIndividualRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoIndividualResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoMembroHistoricoResponseDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.AcompanhamentoMembroItemDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.CelulaResumoDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.AcompanhamentoDiscipuladoColetivo;
import com.gestaoigrejaemcelula.demo.domain.entity.AcompanhamentoDiscipuladoColetivoParticipante;
import com.gestaoigrejaemcelula.demo.domain.entity.AcompanhamentoDiscipuladoIndividual;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusAcompanhamentoDiscipulado;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEstudoDiscipulado;
import com.gestaoigrejaemcelula.demo.domain.repository.AcompanhamentoDiscipuladoColetivoParticipanteRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.AcompanhamentoDiscipuladoColetivoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.AcompanhamentoDiscipuladoIndividualRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import com.gestaoigrejaemcelula.demo.web.handler.BusinessException;
import com.gestaoigrejaemcelula.demo.web.handler.DiscipuladoDuplicadoSemanaException;
import com.gestaoigrejaemcelula.demo.web.handler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcompanhamentoDiscipuladoService {

    public static final String MENSAGEM_DUPLICIDADE =
            "Este membro já foi discipulado nesta semana. Um novo discipulado individual poderá ser registrado somente na próxima semana.";
    private static final String MENSAGEM_SUCESSO_INDIVIDUAL =
            "Discipulado registrado com sucesso! +" + PontuacaoDiscipuladoService.PONTOS_POR_DISCIPULADO + " pontos para a célula.";

    private final AcompanhamentoDiscipuladoIndividualRepository individualRepository;
    private final AcompanhamentoDiscipuladoColetivoRepository coletivoRepository;
    private final AcompanhamentoDiscipuladoColetivoParticipanteRepository participanteRepository;
    private final CelulaRepository celulaRepository;
    private final MembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PontuacaoDiscipuladoService pontuacaoService;

    @Transactional
    @CacheEvict(value = "ranking-celulas", allEntries = true)
    public AcompanhamentoIndividualResponseDTO registrarIndividual(Authentication authentication,
                                                                   AcompanhamentoIndividualRequestDTO dto) {
        YearMonth mesAtual = YearMonth.now();
        YearMonth mesDiscipulado = YearMonth.from(dto.getData());
        if (mesDiscipulado.isBefore(mesAtual)) {
            throw new BusinessException(
                    "Não é permitido registrar discipulados de meses anteriores. O ranking deste mês já foi finalizado.");
        }

        Usuario lider = usuarioLogado(authentication);
        Celula celula = celulaParaEscrita(lider);
        Membro membro = membroRepository.findById(dto.getMembroId())
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));
        validarMembroDaCelula(membro, celula);
        validarTipoEstudo(dto.getTipoEstudo(), dto.getTipoEstudoOutro());

        LocalDate semanaInicio = inicioDaSemana(dto.getData());
        boolean jaDiscipluladoNaSemana = individualRepository.existsByMembro_IdAndSemanaInicioAndStatus(
                membro.getId(), semanaInicio, StatusAcompanhamentoDiscipulado.CONCLUIDO);
        if (jaDiscipluladoNaSemana) {
            throw new DiscipuladoDuplicadoSemanaException(MENSAGEM_DUPLICIDADE);
        }

        AcompanhamentoDiscipuladoIndividual registro = new AcompanhamentoDiscipuladoIndividual();
        registro.setMembro(membro);
        registro.setLider(lider);
        registro.setCelula(celula);
        registro.setData(dto.getData());
        registro.setHorario(dto.getHorario());
        registro.setTipoEstudo(dto.getTipoEstudo());
        registro.setTipoEstudoOutro(normalizar(dto.getTipoEstudoOutro()));
        registro.setTema(dto.getTema());
        registro.setObservacoes(normalizar(dto.getObservacoes()));
        registro.setLocal(normalizar(dto.getLocal()));
        registro.setStatus(StatusAcompanhamentoDiscipulado.CONCLUIDO);
        registro.setSemanaInicio(semanaInicio);
        registro.setSemanaFim(semanaInicio.plusDays(6));
        registro.setCriadoPor(lider.getEmail());

        AcompanhamentoDiscipuladoIndividual salvo = individualRepository.save(registro);

        return paraIndividualResponse(salvo, MENSAGEM_SUCESSO_INDIVIDUAL);
    }

    @Transactional
    @CacheEvict(value = "ranking-celulas", allEntries = true)
    public void cancelarIndividual(Authentication authentication, Long id) {
        Usuario usuario = usuarioLogado(authentication);
        AcompanhamentoDiscipuladoIndividual registro = buscarIndividualComEscopo(usuario, id);
        registro.setStatus(StatusAcompanhamentoDiscipulado.CANCELADO);
        individualRepository.save(registro);
    }

    @Transactional(readOnly = true)
    public AcompanhamentoMembroHistoricoResponseDTO historicoDoMembro(Authentication authentication,
                                                                      Long membroId,
                                                                      Long celulaIdSolicitada) {
        Usuario usuario = usuarioLogado(authentication);
        Celula celula = celulaParaLeitura(usuario, celulaIdSolicitada);

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));
        validarMembroDaCelula(membro, celula);

        List<AcompanhamentoDiscipuladoIndividual> registros =
                individualRepository.findByMembro_IdOrderByDataDescIdDesc(membro.getId());

        List<AcompanhamentoIndividualResponseDTO> itens = registros.stream()
                .map(r -> paraIndividualResponse(r, null))
                .toList();

        List<AcompanhamentoDiscipuladoIndividual> concluidos = registros.stream()
                .filter(r -> r.getStatus() == StatusAcompanhamentoDiscipulado.CONCLUIDO)
                .toList();

        LocalDate ultimo = concluidos.stream()
                .map(AcompanhamentoDiscipuladoIndividual::getData)
                .max(Comparator.naturalOrder())
                .orElse(null);

        LocalDate proximoPeriodo = resolverProximoPeriodoDisponivel(ultimo);

        return AcompanhamentoMembroHistoricoResponseDTO.builder()
                .membroId(membro.getId())
                .membroNome(membro.getNome())
                .totalDiscipulados((long) concluidos.size())
                .totalPontos(pontuacaoService.calcularPontos(concluidos.size()))
                .ultimoDiscipulado(ultimo)
                .proximoPeriodoDisponivel(proximoPeriodo)
                .discipulados(itens)
                .build();
    }

    @Transactional
    @CacheEvict(value = "ranking-celulas", allEntries = true)
    public AcompanhamentoColetivoResponseDTO registrarColetivo(Authentication authentication,
                                                               AcompanhamentoColetivoRequestDTO dto) {
        YearMonth mesAtual = YearMonth.now();
        YearMonth mesColetivo = YearMonth.from(dto.getData());
        if (mesColetivo.isBefore(mesAtual)) {
            throw new BusinessException(
                    "Não é permitido registrar acompanhamentos coletivos de meses anteriores. O ranking deste mês já foi finalizado.");
        }

        Usuario lider = usuarioLogado(authentication);
        Celula celula = celulaParaEscrita(lider);
        validarTipoEstudo(dto.getTipoEstudo(), dto.getTipoEstudoOutro());

        Set<Long> idsPresentes = new LinkedHashSet<>(
                dto.getParticipantesIds() != null ? dto.getParticipantesIds() : List.of());
        if (idsPresentes.isEmpty()) {
            throw new BusinessException("Informe pelo menos um membro presente no encontro");
        }

        List<Membro> presentes = membroRepository.findAllById(idsPresentes);
        if (presentes.size() != idsPresentes.size()) {
            throw new BusinessException("Algum participante informado não existe");
        }
        presentes.forEach(m -> validarMembroDaCelula(m, celula));

        int mes = dto.getData().getMonthValue();
        int ano = dto.getData().getYear();
        boolean jaColetivoNoMes = coletivoRepository.existsByCelula_IdAndMesAndAnoAndStatus(
                celula.getId(), mes, ano, StatusAcompanhamentoDiscipulado.CONCLUIDO.name());
        if (jaColetivoNoMes) {
            throw new BusinessException("Já existe um acompanhamento coletivo registrado nesta célula neste mês. Um novo coletivo poderá ser registrado somente no próximo mês.");
        }

        AcompanhamentoDiscipuladoColetivo coletivo = new AcompanhamentoDiscipuladoColetivo();
        coletivo.setLider(lider);
        coletivo.setCelula(celula);
        coletivo.setData(dto.getData());
        coletivo.setHorario(dto.getHorario());
        coletivo.setTipoEstudo(dto.getTipoEstudo());
        coletivo.setTipoEstudoOutro(normalizar(dto.getTipoEstudoOutro()));
        coletivo.setTema(dto.getTema());
        coletivo.setLocal(normalizar(dto.getLocal()));
        coletivo.setObservacoes(normalizar(dto.getObservacoes()));
        coletivo.setStatus(StatusAcompanhamentoDiscipulado.CONCLUIDO);
        coletivo.setCriadoPor(lider.getEmail());

        presentes.forEach(coletivo::adicionarParticipante);

        AcompanhamentoDiscipuladoColetivo salvo = coletivoRepository.save(coletivo);

        return paraColetivoResponse(salvo, null);
    }

    @Transactional(readOnly = true)
    public AcompanhamentoColetivoResponseDTO detalharColetivo(Authentication authentication, Long id) {
        Usuario usuario = usuarioLogado(authentication);
        AcompanhamentoDiscipuladoColetivo coletivo = coletivoRepository.buscarComParticipantesPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discipulado coletivo não encontrado"));
        garantirAcessoLeituraCelula(usuario, coletivo.getCelula().getId());
        return paraColetivoResponse(coletivo, null);
    }

    @Transactional
    @CacheEvict(value = "ranking-celulas", allEntries = true)
    public void cancelarColetivo(Authentication authentication, Long id) {
        Usuario usuario = usuarioLogado(authentication);
        AcompanhamentoDiscipuladoColetivo coletivo = coletivoRepository.buscarComParticipantesPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discipulado coletivo não encontrado"));
        if (usuario.getPerfil() != Perfil.ADMIN) {
            Celula celula = celulaParaEscrita(usuario);
            if (!coletivo.getCelula().getId().equals(celula.getId())) {
                throw new BusinessException("Você só pode alterar registros da sua própria célula");
            }
        }
        coletivo.setStatus(StatusAcompanhamentoDiscipulado.CANCELADO);
        coletivoRepository.save(coletivo);
    }

    @Transactional(readOnly = true)
    public List<CelulaResumoDTO> listarCelulasDoPastor(Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        return celulasAcessiveisAoPastor(usuario, null).stream()
                .map(CelulaResumoDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AcompanhamentoHistoricoItemDTO> historicoPastor(Authentication authentication,
                                                                Long celulaIdSolicitada,
                                                                LocalDate inicio,
                                                                LocalDate fim,
                                                                Long membroId,
                                                                String tipo,
                                                                String tema,
                                                                TipoEstudoDiscipulado tipoEstudo) {
        Usuario usuario = usuarioLogado(authentication);
        List<Long> celulaIds = celulasAcessiveisAoPastor(usuario, celulaIdSolicitada)
                .stream().map(Celula::getId).toList();

        if (celulaIds.isEmpty()) {
            return List.of();
        }

        boolean filtrarIndividual = tipo == null || "INDIVIDUAL".equalsIgnoreCase(tipo);
        boolean filtrarColetivo = tipo == null || "COLETIVO".equalsIgnoreCase(tipo);

        List<AcompanhamentoHistoricoItemDTO> itens = new ArrayList<>();

        if (filtrarIndividual) {
            individualRepository.buscarPorCelulasComFiltros(celulaIds, membroId, inicio, fim, tema, tipoEstudo)
                    .forEach(r -> itens.add(paraHistoricoItem(r)));
        }
        if (filtrarColetivo) {
            coletivoRepository.buscarPorCelulasComFiltros(celulaIds, membroId, inicio, fim, tema, tipoEstudo)
                    .forEach(c -> itens.add(paraHistoricoItem(c)));
        }

        return itens.stream()
                .sorted(Comparator.comparing(AcompanhamentoHistoricoItemDTO::getData).reversed()
                        .thenComparing(AcompanhamentoHistoricoItemDTO::getId, Comparator.reverseOrder()))
                .toList();
    }

    private List<Celula> celulasAcessiveisAoPastor(Usuario usuario, Long celulaIdSolicitada) {
        if (usuario.getPerfil() == Perfil.ADMIN || usuario.getPerfil() == Perfil.PASTOR) {
            if (celulaIdSolicitada != null) {
                Celula celula = celulaRepository.findById(celulaIdSolicitada)
                        .orElseThrow(() -> new ResourceNotFoundException("Célula não encontrada"));
                return List.of(celula);
            }
            return celulaRepository.findByAtivaTrue();
        }
        throw new BusinessException("Acesso restrito a pastores e administradores");
    }

    @Transactional(readOnly = true)
    public List<AcompanhamentoMembroItemDTO> listarMembrosDaCelula(Authentication authentication,
                                                                   Long celulaIdSolicitada) {
        Usuario usuario = usuarioLogado(authentication);
        Celula celula = celulaParaLeitura(usuario, celulaIdSolicitada);
        LocalDate inicioSemanaAtual = inicioDaSemana(LocalDate.now());

        Map<Long, List<AcompanhamentoDiscipuladoIndividual>> individuaisPorMembro =
                agruparIndividuaisConcluidos(individualRepository.findByCelula_Id(celula.getId()));

        Map<Long, List<AcompanhamentoDiscipuladoColetivoParticipante>> presencasPorMembro =
                agruparPresencas(participanteRepository.findByDiscipulado_Celula_Id(celula.getId()));

        return membrosAtivos(celula).stream()
                .map(membro -> montarItemMembro(
                        membro,
                        individuaisPorMembro.getOrDefault(membro.getId(), List.of()),
                        presencasPorMembro.getOrDefault(membro.getId(), List.of()),
                        inicioSemanaAtual))
                .toList();
    }

    @Transactional(readOnly = true)
    public AcompanhamentoIndicadoresDTO indicadores(Authentication authentication, Long celulaIdSolicitada) {
        Usuario usuario = usuarioLogado(authentication);
        Celula celula = celulaParaLeitura(usuario, celulaIdSolicitada);

        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = inicioDaSemana(hoje);
        LocalDate fimSemana = inicioSemana.plusDays(6);
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        List<Membro> ativos = membrosAtivos(celula);

        List<AcompanhamentoDiscipuladoIndividual> individuais =
                individualRepository.findByCelula_Id(celula.getId()).stream()
                        .filter(r -> r.getStatus() == StatusAcompanhamentoDiscipulado.CONCLUIDO)
                        .toList();

        List<AcompanhamentoDiscipuladoColetivo> coletivos =
                coletivoRepository.buscarComFiltros(celula.getId(), null, null, null, null, null).stream()
                        .filter(c -> c.getStatus() == StatusAcompanhamentoDiscipulado.CONCLUIDO)
                        .toList();

        long individuaisSemana = individuais.stream()
                .filter(r -> !r.getData().isBefore(inicioSemana) && !r.getData().isAfter(fimSemana)).count();
        long individuaisMes = individuais.stream()
                .filter(r -> !r.getData().isBefore(inicioMes) && !r.getData().isAfter(fimMes)).count();
        long coletivosMes = coletivos.stream()
                .filter(c -> !c.getData().isBefore(inicioMes) && !c.getData().isAfter(fimMes)).count();

        long participacoesSemana = coletivos.stream()
                .filter(c -> !c.getData().isBefore(inicioSemana) && !c.getData().isAfter(fimSemana))
                .mapToLong(c -> c.getParticipantes().size()).sum();
        long participacoesTotal = coletivos.stream()
                .mapToLong(c -> c.getParticipantes().size()).sum();

        int pontosSemana = pontuacaoService.calcularPontos((int) individuaisSemana)
                + pontuacaoService.calcularPontos((int) participacoesSemana);
        int pontosTotais = pontuacaoService.calcularPontos(individuais.size())
                + pontuacaoService.calcularPontos((int) participacoesTotal);

        Set<Long> membrosJaDiscipluladosEstaSemana = individuais.stream()
                .filter(r -> r.getSemanaInicio().equals(inicioSemana))
                .map(r -> r.getMembro().getId())
                .collect(Collectors.toSet());

        List<String> pendentesNomes = ativos.stream()
                .filter(m -> !membrosJaDiscipluladosEstaSemana.contains(m.getId()))
                .map(Membro::getNome)
                .toList();

        Set<Long> membrosDiscipluladosIds = new HashSet<>();
        individuais.forEach(r -> membrosDiscipluladosIds.add(r.getMembro().getId()));
        coletivos.forEach(c -> c.getParticipantes()
                .forEach(p -> membrosDiscipluladosIds.add(p.getMembro().getId())));

        return AcompanhamentoIndicadoresDTO.builder()
                .pontosDiscipuladoSemana(pontosSemana)
                .pontosDiscipuladoMes(calcularPontosDoPeriodo(individuais, coletivos, inicioMes, fimMes))
                .discipuladosIndividuaisSemana(individuaisSemana)
                .discipuladosIndividuaisMes(individuaisMes)
                .discipuladosColetivosMes(coletivosMes)
                .discipuladosColetivosTotal((long) coletivos.size())
                .participacoesColetivasTotal(participacoesTotal)
                .totalDiscipulados(individuais.size() + (long) coletivos.size())
                .totalPontos(pontosTotais)
                .membrosDiscipulados((long) membrosDiscipluladosIds.size())
                .membrosNaoDiscipuladosSemana((long) pendentesNomes.size())
                .nomesMembrosPendentesSemana(pendentesNomes)
                .totalMembrosAtivos((long) ativos.size())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AcompanhamentoHistoricoItemDTO> historicoGeral(Authentication authentication,
                                                               Long celulaIdSolicitada,
                                                               LocalDate inicio,
                                                               LocalDate fim,
                                                               Long membroId,
                                                               String tipo,
                                                               String tema,
                                                               TipoEstudoDiscipulado tipoEstudo) {
        Usuario usuario = usuarioLogado(authentication);
        Long celulaId = resolverCelulaIdParaConsulta(usuario, celulaIdSolicitada);

        boolean filtrarIndividual = tipo == null || "INDIVIDUAL".equalsIgnoreCase(tipo);
        boolean filtrarColetivo = tipo == null || "COLETIVO".equalsIgnoreCase(tipo);

        List<AcompanhamentoHistoricoItemDTO> itens = new ArrayList<>();

        if (filtrarIndividual) {
            individualRepository.buscarComFiltros(celulaId, membroId, inicio, fim, tema, tipoEstudo)
                    .forEach(r -> itens.add(paraHistoricoItem(r)));
        }
        if (filtrarColetivo) {
            coletivoRepository.buscarComFiltros(celulaId, membroId, inicio, fim, tema, tipoEstudo)
                    .forEach(c -> itens.add(paraHistoricoItem(c)));
        }

        return itens.stream()
                .sorted(Comparator.comparing(AcompanhamentoHistoricoItemDTO::getData).reversed()
                        .thenComparing(AcompanhamentoHistoricoItemDTO::getId, Comparator.reverseOrder()))
                .toList();
    }


    private AcompanhamentoMembroItemDTO montarItemMembro(Membro membro,
                                                         List<AcompanhamentoDiscipuladoIndividual> individuais,
                                                         List<AcompanhamentoDiscipuladoColetivoParticipante> presencas,
                                                         LocalDate inicioSemanaAtual) {
        LocalDate ultimo = individuais.stream()
                .map(AcompanhamentoDiscipuladoIndividual::getData)
                .max(Comparator.naturalOrder())
                .orElse(null);

        boolean discipluladoEstaSemana = individuais.stream()
                .anyMatch(r -> r.getSemanaInicio().equals(inicioSemanaAtual));

        int pontosIndividuais = pontuacaoService.calcularPontos(individuais.size());
        int pontosColetivos = pontuacaoService.calcularPontos(presencas.size());

        return AcompanhamentoMembroItemDTO.builder()
                .membroId(membro.getId())
                .membroNome(membro.getNome())
                .totalDiscipuladosIndividuais((long) individuais.size())
                .pontosIndividuais(pontosIndividuais)
                .participacoesColetivas((long) presencas.size())
                .pontosColetivos(pontosColetivos)
                .totalPontos(pontosIndividuais + pontosColetivos)
                .ultimoDiscipulado(ultimo)
                .discipuladoEstaSemana(discipluladoEstaSemana)
                .proximoPeriodoDisponivel(discipluladoEstaSemana ? inicioSemanaAtual.plusWeeks(1) : null)
                .statusSemanal(discipluladoEstaSemana ? "REALIZADO" : "PENDENTE")
                .mensagemStatus(discipluladoEstaSemana ? "Membro já foi discipulado nesta semana." : "Membro ainda não discipulado nesta semana.")
                .build();
    }

    private Map<Long, List<AcompanhamentoDiscipuladoIndividual>> agruparIndividuaisConcluidos(
            List<AcompanhamentoDiscipuladoIndividual> registros) {
        return registros.stream()
                .filter(r -> r.getStatus() == StatusAcompanhamentoDiscipulado.CONCLUIDO)
                .collect(Collectors.groupingBy(r -> r.getMembro().getId()));
    }

    private Map<Long, List<AcompanhamentoDiscipuladoColetivoParticipante>> agruparPresencas(
            List<AcompanhamentoDiscipuladoColetivoParticipante> participacoes) {
        return participacoes.stream()
                .filter(p -> p.getDiscipulado().getStatus() == StatusAcompanhamentoDiscipulado.CONCLUIDO)
                .collect(Collectors.groupingBy(p -> p.getMembro().getId()));
    }

    private List<Membro> membrosAtivos(Celula celula) {
        return membroRepository.findByCelulaId(celula.getId()).stream()
                .filter(m -> m.getStatus() == StatusMembro.ATIVO)
                .sorted(Comparator.comparing(Membro::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private int calcularPontosDoPeriodo(List<AcompanhamentoDiscipuladoIndividual> individuais,
                                        List<AcompanhamentoDiscipuladoColetivo> coletivos,
                                        LocalDate inicio,
                                        LocalDate fim) {
        long individuaisNoPeriodo = individuais.stream()
                .filter(r -> !r.getData().isBefore(inicio) && !r.getData().isAfter(fim)).count();
        long participacoesNoPeriodo = coletivos.stream()
                .filter(c -> !c.getData().isBefore(inicio) && !c.getData().isAfter(fim))
                .mapToLong(c -> c.getParticipantes().size()).sum();
        return pontuacaoService.calcularPontos((int) individuaisNoPeriodo)
                + pontuacaoService.calcularPontos((int) participacoesNoPeriodo);
    }

    private LocalDate resolverProximoPeriodoDisponivel(LocalDate ultimoDiscipulado) {
        if (ultimoDiscipulado == null) {
            return null;
        }
        LocalDate inicioSemanaUltimo = inicioDaSemana(ultimoDiscipulado);
        LocalDate inicioSemanaAtual = inicioDaSemana(LocalDate.now());
        return inicioSemanaUltimo.equals(inicioSemanaAtual) ? inicioSemanaAtual.plusWeeks(1) : null;
    }

    private AcompanhamentoIndividualResponseDTO paraIndividualResponse(
            AcompanhamentoDiscipuladoIndividual r, String mensagem) {
        boolean concluido = r.getStatus() == StatusAcompanhamentoDiscipulado.CONCLUIDO;
        return AcompanhamentoIndividualResponseDTO.builder()
                .id(r.getId())
                .membroId(r.getMembro().getId())
                .membroNome(r.getMembro().getNome())
                .liderId(r.getLider().getId())
                .liderNome(r.getLider().getNome())
                .celulaId(r.getCelula().getId())
                .celulaNome(r.getCelula().getNome())
                .data(r.getData())
                .horario(r.getHorario())
                .tipoEstudo(r.getTipoEstudo().name())
                .tipoEstudoDescricao(r.getTipoEstudo().getDescricao())
                .tipoEstudoOutro(r.getTipoEstudoOutro())
                .tema(r.getTema())
                .observacoes(r.getObservacoes())
                .local(r.getLocal())
                .status(r.getStatus())
                .pontosGerados(concluido ? PontuacaoDiscipuladoService.PONTOS_POR_DISCIPULADO : 0)
                .criadoEm(r.getCriadoEm())
                .atualizadoEm(r.getAtualizadoEm())
                .mensagem(mensagem)
                .build();
    }

    private AcompanhamentoColetivoResponseDTO paraColetivoResponse(
            AcompanhamentoDiscipuladoColetivo c, String mensagem) {
        boolean concluido = c.getStatus() == StatusAcompanhamentoDiscipulado.CONCLUIDO;
        int quantidade = c.getParticipantes().size();
        int pontos = pontuacaoService.calcularPontos(quantidade);
        return AcompanhamentoColetivoResponseDTO.builder()
                .id(c.getId())
                .liderId(c.getLider().getId())
                .liderNome(c.getLider().getNome())
                .celulaId(c.getCelula().getId())
                .celulaNome(c.getCelula().getNome())
                .data(c.getData())
                .horario(c.getHorario())
                .tipoEstudo(c.getTipoEstudo().name())
                .tipoEstudoDescricao(c.getTipoEstudo().getDescricao())
                .tipoEstudoOutro(c.getTipoEstudoOutro())
                .tema(c.getTema())
                .local(c.getLocal())
                .observacoes(c.getObservacoes())
                .status(c.getStatus())
                .presentes(c.getParticipantes().stream()
                        .map(p -> new AcompanhamentoColetivoResponseDTO.AcompanhamentoParticipanteItemDTO(
                                p.getMembro().getId(), p.getMembro().getNome()))
                        .toList())
                .quantidadePresentes(quantidade)
                .pontosGerados(concluido ? pontos : 0)
                .formulaPontuacao(quantidade + " × " + PontuacaoDiscipuladoService.PONTOS_POR_DISCIPULADO
                        + " = " + (concluido ? pontos : 0))
                .criadoEm(c.getCriadoEm())
                .atualizadoEm(c.getAtualizadoEm())
                .mensagem(mensagem != null ? mensagem
                        : "Discipulado coletivo registrado com sucesso! " + quantidade
                        + " participantes foram contabilizados. +" + (concluido ? pontos : 0)
                        + " pontos para a célula.")
                .build();
    }

    private AcompanhamentoHistoricoItemDTO paraHistoricoItem(AcompanhamentoDiscipuladoIndividual r) {
        boolean concluido = r.getStatus() == StatusAcompanhamentoDiscipulado.CONCLUIDO;
        return AcompanhamentoHistoricoItemDTO.builder()
                .id(r.getId())
                .tipo("INDIVIDUAL")
                .celulaId(r.getCelula().getId())
                .celulaNome(r.getCelula().getNome())
                .data(r.getData())
                .membroId(r.getMembro().getId())
                .membroNome(r.getMembro().getNome())
                .quantidadeParticipantes(1)
                .tema(r.getTema())
                .tipoEstudoDescricao(r.getTipoEstudo().getDescricao())
                .liderId(r.getLider().getId())
                .liderNome(r.getLider().getNome())
                .status(r.getStatus().name())
                .pontos(concluido ? PontuacaoDiscipuladoService.PONTOS_POR_DISCIPULADO : 0)
                .build();
    }

    private AcompanhamentoHistoricoItemDTO paraHistoricoItem(AcompanhamentoDiscipuladoColetivo c) {
        boolean concluido = c.getStatus() == StatusAcompanhamentoDiscipulado.CONCLUIDO;
        int quantidade = c.getParticipantes().size();
        return AcompanhamentoHistoricoItemDTO.builder()
                .id(c.getId())
                .tipo("COLETIVO")
                .celulaId(c.getCelula().getId())
                .celulaNome(c.getCelula().getNome())
                .data(c.getData())
                .quantidadeParticipantes(quantidade)
                .tema(c.getTema())
                .tipoEstudoDescricao(c.getTipoEstudo().getDescricao())
                .liderId(c.getLider().getId())
                .liderNome(c.getLider().getNome())
                .status(c.getStatus().name())
                .pontos(concluido ? pontuacaoService.calcularPontos(quantidade) : 0)
                .build();
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new BusinessException("Usuário autenticado não encontrado"));
    }

    private Celula celulaParaEscrita(Usuario usuario) {
        if (usuario.getPerfil() != Perfil.LIDER_CELULA) {
            throw new BusinessException("Somente o líder da célula pode registrar discipulados");
        }
        return celulaRepository.findByLider_IdAndAtivaTrue(usuario.getId())
                .orElseThrow(() -> new BusinessException("Você não possui uma célula ativa como líder"));
    }

    private Celula celulaParaLeitura(Usuario usuario, Long celulaIdSolicitada) {
        if (usuario.getPerfil() == Perfil.ADMIN || usuario.getPerfil() == Perfil.PASTOR) {
            if (celulaIdSolicitada == null) {
                throw new BusinessException("Informe o parâmetro celulaId para consultar os dados da célula");
            }
            return celulaRepository.findById(celulaIdSolicitada)
                    .orElseThrow(() -> new ResourceNotFoundException("Célula não encontrada"));
        }
        if (usuario.getPerfil() != Perfil.LIDER_CELULA) {
            throw new BusinessException("Apenas líderes podem acessar este módulo");
        }
        if (celulaIdSolicitada != null) {
            throw new BusinessException("Você só pode consultar os dados da sua própria célula");
        }
        return celulaRepository.findByLider_IdAndAtivaTrue(usuario.getId())
                .orElseThrow(() -> new BusinessException("Você não possui uma célula ativa como líder"));
    }

    private Long resolverCelulaIdParaConsulta(Usuario usuario, Long celulaIdSolicitada) {
        return celulaParaLeitura(usuario, celulaIdSolicitada).getId();
    }

    private void garantirAcessoLeituraCelula(Usuario usuario, Long celulaIdDoRegistro) {
        if (usuario.getPerfil() == Perfil.ADMIN || usuario.getPerfil() == Perfil.PASTOR) {
            return;
        }
        Celula celula = celulaParaLeitura(usuario, null);
        if (!celula.getId().equals(celulaIdDoRegistro)) {
            throw new BusinessException("Você só pode consultar registros da sua própria célula");
        }
    }

    private AcompanhamentoDiscipuladoIndividual buscarIndividualComEscopo(Usuario usuario, Long id) {
        if (usuario.getPerfil() == Perfil.ADMIN) {
            return individualRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Discipulado individual não encontrado"));
        }
        Celula celula = celulaParaEscrita(usuario);
        return individualRepository.findByIdAndCelula_Id(id, celula.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Discipulado individual não encontrado"));
    }

    private void validarMembroDaCelula(Membro membro, Celula celula) {
        if (membro.getCelula() == null || !celula.getId().equals(membro.getCelula().getId())) {
            throw new BusinessException("Este membro não pertence à sua célula");
        }
        if (membro.getStatus() != StatusMembro.ATIVO) {
            throw new BusinessException("Somente membros ativos podem ser discipulados");
        }
    }

    private void validarTipoEstudo(TipoEstudoDiscipulado tipoEstudo, String tipoEstudoOutro) {
        if (tipoEstudo == TipoEstudoDiscipulado.OUTRO
                && (tipoEstudoOutro == null || tipoEstudoOutro.isBlank())) {
            throw new BusinessException("Informe a descrição quando o tipo de estudo for 'Outro'");
        }
    }

    private LocalDate inicioDaSemana(LocalDate data) {
        return data.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private String normalizar(String valor) {
        return valor != null && !valor.isBlank() ? valor.trim() : null;
    }
}
