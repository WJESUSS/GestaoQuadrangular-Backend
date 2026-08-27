package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.*;
import com.gestaoigrejaemcelula.demo.domain.entity.Culto;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.enums.Perfil;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoCulto;
import com.gestaoigrejaemcelula.demo.domain.repository.CultoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import com.gestaoigrejaemcelula.demo.web.handler.BusinessException;
import com.gestaoigrejaemcelula.demo.web.handler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CultoService {

    private static final String ENTIDADE = "CULTO";
    private static final int DIAS_JANELA_EDICAO = 3;
    private static final double PERCENTUAL_ALERTA = 0.20;

    private final CultoRepository cultoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaHelper auditoria;
    private final NotificacaoService notificacaoService;

    // ═══════════════════════════════════════════════════════════════════
    // TIPO SUGERIDO
    // ═══════════════════════════════════════════════════════════════════

    public static TipoCulto calcularTipoCulto(LocalDate data) {
        DayOfWeek diaSemana = data.getDayOfWeek();

        if (diaSemana == DayOfWeek.WEDNESDAY || diaSemana == DayOfWeek.THURSDAY) {
            return TipoCulto.VITORIA;
        }

        if (diaSemana == DayOfWeek.SUNDAY) {
            int semanaDoMes = data.getDayOfMonth();
            LocalDate primeiroDomingo = data.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));
            int ordinalDomingo = (int) ((data.getDayOfMonth() - primeiroDomingo.getDayOfMonth()) / 7) + 1;

            if (ordinalDomingo == 1) return TipoCulto.SANTA_CEIA;
            if (ordinalDomingo == 2) return TipoCulto.CELEBRACAO;
            if (ordinalDomingo == 3) return TipoCulto.MISSOES;
            return TipoCulto.CELEBRACAO;
        }

        return null;
    }

    // ═══════════════════════════════════════════════════════════════════
    // CRIAR
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public CultoResponseDTO criar(CultoRequestDTO dto) {
        validarDados(dto);

        if (dto.getTipoCulto() == null) {
            TipoCulto sugerido = calcularTipoCulto(dto.getData());
            if (sugerido == null) {
                throw new BusinessException("Tipo de culto é obrigatório para datas que não são quarta/quinta/domingu.");
            }
            dto.setTipoCulto(sugerido);
        }

        cultoRepository.findByDataAndTipoCultoAndHorario(dto.getData(), dto.getTipoCulto(), dto.getHorario())
                .ifPresent(c -> {
                    throw new BusinessException(
                            "Já existe um registro de culto para esta data, tipo e horário. Use PUT para editar.");
                });

        Culto culto = new Culto();
        copiarDtoParaEntidade(dto, culto);
        culto.setRegistradoPor(buscarUsuarioLogado());

        Culto salvo = cultoRepository.save(culto);
        auditoria.registrar(ENTIDADE, salvo.getId().toString(), salvo.getData().toString(), "CREATE", null);

        verificarAlerta(salvo);

        return new CultoResponseDTO(salvo);
    }

    // ═══════════════════════════════════════════════════════════════════
    // EDITAR
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public CultoResponseDTO editar(UUID id, CultoRequestDTO dto) {
        Culto culto = buscarEntidadePorId(id);
        Usuario usuarioLogado = buscarUsuarioLogado();

        if (!isPastorOuAdmin(usuarioLogado)) {
            if (!culto.getRegistradoPor().getId().equals(usuarioLogado.getId())) {
                throw new AccessDeniedException("Você só pode editar cultos que você mesmo registrou.");
            }
            if (culto.getData().plusDays(DIAS_JANELA_EDICAO).isBefore(LocalDate.now())) {
                throw new BusinessException(
                        "Janela de edição expirada (>" + DIAS_JANELA_EDICAO + " dias). Solicite ao pastor.");
            }
        }

        validarDados(dto);

        if (dto.getTipoCulto() == null) {
            dto.setTipoCulto(culto.getTipoCulto());
        }

        if (dto.getHorario() == null || dto.getHorario().isBlank()) {
            dto.setHorario(culto.getHorario());
        }

        validarTipoCultoPorDia(dto.getData(), dto.getTipoCulto());

        cultoRepository.findByDataAndTipoCultoAndHorario(dto.getData(), dto.getTipoCulto(), dto.getHorario())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new BusinessException("Já existe outro registro para esta data, tipo e horário.");
                });

        copiarDtoParaEntidade(dto, culto);
        Culto salvo = cultoRepository.save(culto);

        auditoria.registrar(ENTIDADE, salvo.getId().toString(), salvo.getData().toString(), "UPDATE", null);

        return new CultoResponseDTO(salvo);
    }

    // ═══════════════════════════════════════════════════════════════════
    // EXCLUIR
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public void excluir(UUID id) {
        Culto culto = buscarEntidadePorId(id);
        Usuario usuarioLogado = buscarUsuarioLogado();

        if (!isPastorOuAdmin(usuarioLogado)) {
            throw new AccessDeniedException("Somente pastor/admin pode excluir registros de culto.");
        }

        cultoRepository.deleteById(id);
        auditoria.registrar(ENTIDADE, id.toString(), culto.getData().toString(), "DELETE", null);
    }

    // ═══════════════════════════════════════════════════════════════════
    // BUSCAR POR ID
    // ═══════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public CultoResponseDTO buscarPorId(UUID id) {
        Culto culto = buscarEntidadePorId(id);
        Usuario usuarioLogado = buscarUsuarioLogado();

        if (!isPastorOuAdmin(usuarioLogado)
                && !culto.getRegistradoPor().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Você só pode visualizar cultos que você mesmo registrou.");
        }

        return new CultoResponseDTO(culto);
    }

    // ═══════════════════════════════════════════════════════════════════
    // LISTAR COM FILTROS
    // ═══════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public Page<CultoResponseDTO> listar(CultoFiltrosDTO filtros, Pageable pageable) {
        LocalDate dataInicio = parseData(filtros.getDataInicio());
        LocalDate dataFim = parseData(filtros.getDataFim());
        TipoCulto tipoCulto = parseTipoCulto(filtros.getTipoCulto());

        Usuario usuarioLogado = buscarUsuarioLogado();
        Long registradoPorFiltro = filtros.getRegistradoPor();

        if (!isPastorOuAdmin(usuarioLogado) && registradoPorFiltro == null) {
            registradoPorFiltro = usuarioLogado.getId();
        }

        return cultoRepository.filtrar(
                dataInicio, dataFim, tipoCulto,
                filtros.getPregador(), filtros.getCampanha(),
                registradoPorFiltro, pageable
        ).map(CultoResponseDTO::new);
    }

    // ═══════════════════════════════════════════════════════════════════
    // RELATÓRIOS
    // ═══════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public CultoRelatorioResumoDTO relatorioResumo(LocalDate dataInicio, LocalDate dataFim) {
        CultoRelatorioResumoDTO resumo = cultoRepository.resumo(dataInicio, dataFim);
        return resumo;
    }

    @Transactional(readOnly = true)
    public Page<CultoRelatorioComparativoDTO> relatorioComparativo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return cultoRepository.comparativo(dataInicio, dataFim, pageable);
    }

    @Transactional(readOnly = true)
    public Page<CultoRelatorioCampanhaDTO> relatorioCampanhas(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return cultoRepository.porCampanha(dataInicio, dataFim, pageable);
    }

    @Transactional(readOnly = true)
    public Page<CultoRelatorioPregadorDTO> relatorioPregadores(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return cultoRepository.porPregador(dataInicio, dataFim, pageable);
    }

    // ═══════════════════════════════════════════════════════════════════
    // LISTA DE CULTOS PARA PDF
    // ═══════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<Culto> buscarCultosParaPdf(LocalDate dataInicio, LocalDate dataFim,
                                           TipoCulto tipoCulto, String pregador) {
        return cultoRepository.filtrar(dataInicio, dataFim, tipoCulto, pregador, null, null,
                Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    public Culto buscarCultoParaPdf(UUID id) {
        return buscarEntidadePorId(id);
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITÁRIOS / VALIDAÇÕES
    // ═══════════════════════════════════════════════════════════════════

    private void validarDados(CultoRequestDTO dto) {
        if (dto.getData().isAfter(LocalDate.now())) {
            throw new BusinessException("Data do culto não pode ser futura.");
        }
        if (dto.getQuantidadeMembros() < 0) throw new BusinessException("Quantidade de membros deve ser ≥ 0.");
        if (dto.getVisitantesSimpatizantes() < 0) throw new BusinessException("Visitantes/simpatizantes deve ser ≥ 0.");
        if (dto.getTotalCriancas() < 0) throw new BusinessException("Total de crianças deve ser ≥ 0.");
        if (dto.getQuantidadeDiaconos() < 0) throw new BusinessException("Quantidade de diáconos deve ser ≥ 0.");

        if (Boolean.TRUE.equals(dto.getCampanhaAtiva())
                && (dto.getNomeCampanha() == null || dto.getNomeCampanha().isBlank())) {
            throw new BusinessException("Nome da campanha é obrigatório quando campanha está ativa.");
        }

        if (dto.getTipoCulto() != null) {
            validarTipoCultoPorDia(dto.getData(), dto.getTipoCulto());
        }
    }

    private void validarTipoCultoPorDia(LocalDate data, TipoCulto tipoCulto) {
        DayOfWeek diaSemana = data.getDayOfWeek();
        boolean isDomingo = diaSemana == DayOfWeek.SUNDAY;
        boolean isQuartaQuinta = diaSemana == DayOfWeek.WEDNESDAY || diaSemana == DayOfWeek.THURSDAY;

        if (isDomingo && tipoCulto == TipoCulto.VITORIA) {
            throw new BusinessException("Culto da Vitória não pode ser registrado em domingo. É permitido apenas quarta e quinta-feira.");
        }

        if (isQuartaQuinta && tipoCulto != TipoCulto.VITORIA) {
            throw new BusinessException("Cultos de " + tipoCulto.getDescricao() + " não podem ser registrados em quarta/quinta-feira. É permitido apenas aos domingos.");
        }
    }

    private void copiarDtoParaEntidade(CultoRequestDTO dto, Culto culto) {
        culto.setData(dto.getData());
        culto.setTipoCulto(dto.getTipoCulto());
        culto.setHorario(dto.getHorario());
        culto.setTextoPregado(dto.getTextoPregado());
        culto.setPregador(dto.getPregador());
        culto.setQuantidadeMembros(dto.getQuantidadeMembros());
        culto.setVisitantesSimpatizantes(dto.getVisitantesSimpatizantes());
        culto.setTotalCriancas(dto.getTotalCriancas());
        culto.setQuantidadeDiaconos(dto.getQuantidadeDiaconos());
        culto.setCampanhaAtiva(dto.getCampanhaAtiva());
        culto.setNomeCampanha(dto.getNomeCampanha());
        culto.setObservacoes(dto.getObservacoes());
        culto.calcularTotalGeral();
    }

    private Culto buscarEntidadePorId(UUID id) {
        return cultoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culto não encontrado: " + id));
    }

    private Usuario buscarUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        String email = auth.getName();
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
    }

    private boolean isPastorOuAdmin(Usuario usuario) {
        return usuario.getPerfil() == Perfil.ADMIN
                || usuario.getPerfil() == Perfil.PASTOR
                || usuario.getPerfil() == Perfil.TESOUREIRO;
    }

    private LocalDate parseData(String dataStr) {
        if (dataStr == null || dataStr.isBlank()) return null;
        return LocalDate.parse(dataStr);
    }

    private TipoCulto parseTipoCulto(String tipoStr) {
        if (tipoStr == null || tipoStr.isBlank()) return null;
        return TipoCulto.fromString(tipoStr);
    }

    // ═══════════════════════════════════════════════════════════════════
    // ALERTA AUTOMÁTICO
    // ═══════════════════════════════════════════════════════════════════

    private void verificarAlerta(Culto culto) {
        Double media = cultoRepository.mediaTotalGeralPorTipoAteData(
                culto.getTipoCulto(), culto.getData());

        if (media == null || media == 0) return;

        double percentual = (double) culto.getTotalGeral() / media;
        if (percentual < (1.0 - PERCENTUAL_ALERTA)) {
            List<Usuario> pastores = usuarioRepository.findByPerfilIn(List.of(Perfil.PASTOR, Perfil.ADMIN));
            String titulo = "Alerta: Presença abaixo da média";
            String mensagem = String.format(
                    "O culto de %s (%s) teve total de %d pessoas, %.0f%% abaixo da média de %.0f do tipo %s.",
                    culto.getData(), culto.getPregador(),
                    culto.getTotalGeral(),
                    (1 - percentual) * 100,
                    media,
                    culto.getTipoCulto().getDescricao());

            for (Usuario pastor : pastores) {
                notificacaoService.enviarNotificacao(
                        pastor.getId(), titulo, mensagem,
                        com.gestaoigrejaemcelula.demo.domain.entity.Notificacao.TipoNotificacao.AVISO_GERAL);
            }
        }
    }
}
