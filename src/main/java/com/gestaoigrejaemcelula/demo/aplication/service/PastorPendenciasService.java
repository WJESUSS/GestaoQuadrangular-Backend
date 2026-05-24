package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoRelatorioRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.RelatorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PastorPendenciasService {

    private final CelulaRepository              celulaRepository;
    private final RelatorioRepository            relatorioRepository;
    private final DiscipuladoRelatorioRepository discipuladoRepository;

    public PastorPendenciasService(
            CelulaRepository celulaRepository,
            RelatorioRepository relatorioRepository,
            DiscipuladoRelatorioRepository discipuladoRepository
    ) {
        this.celulaRepository    = celulaRepository;
        this.relatorioRepository = relatorioRepository;
        this.discipuladoRepository = discipuladoRepository;
    }

    // =============================================================
    // LISTAR — com suporte a semana específica e exibição de todas
    //
    // semanaInicio → se null, usa a semana atual
    // todas        → se true, retorna também células em dia
    // =============================================================
    @Transactional(readOnly = true)
    public List<PendenciaDTO> listarPendencias(LocalDate semanaInicio, boolean todas) {

        // Se não vier data, usa a semana atual (segunda → domingo)
        LocalDate inicio = (semanaInicio != null)
                ? semanaInicio.with(WeekFields.ISO.dayOfWeek(), 1)  // garante que é segunda
                : LocalDate.now().with(WeekFields.ISO.dayOfWeek(), 1);

        LocalDate fim = inicio.with(WeekFields.ISO.dayOfWeek(), 7);

        // Todas as células ativas
        List<Celula> celulas = celulaRepository.findAllByAtivaTrue();

        // IDs que já entregaram relatório nesta semana
        Set<Long> comRelatorio = relatorioRepository
                .findByDataReuniaoBetween(inicio, fim)
                .stream()
                .map(r -> r.getCelula().getId())
                .collect(Collectors.toSet());

        // IDs que já entregaram discipulado nesta semana
        Set<Long> comDiscipulado = discipuladoRepository
                .findBySemanaInicioBetween(inicio, fim)
                .stream()
                .map(d -> d.getCelula().getId())
                .collect(Collectors.toSet());

        LocalDate inicioFinal = inicio;
        LocalDate fimFinal    = fim;

        return celulas.stream()
                .map(c -> new PendenciaDTO(
                        c.getId(),
                        c.getNome(),
                        c.getLider() != null ? c.getLider().getNome() : "Sem líder",
                        c.getBairro(),
                        !comRelatorio.contains(c.getId()),
                        !comDiscipulado.contains(c.getId()),
                        inicioFinal.toString(),
                        fimFinal.toString()
                ))
                // Se todas=false → só pendentes; se todas=true → todas as células
                .filter(dto -> todas || dto.isRelatorioPendente() || dto.isDiscipuladoPendente())
                // Ordenação: ambas pendentes → só uma pendente → em dia; depois por nome
                .sorted(Comparator
                        .comparingInt((PendenciaDTO d) ->
                                (d.isRelatorioPendente() ? 1 : 0) + (d.isDiscipuladoPendente() ? 1 : 0))
                        .reversed()
                        .thenComparing(PendenciaDTO::getNomeCelula))
                .collect(Collectors.toList());
    }

    // Atalho para manter compatibilidade com chamadas antigas (semana atual, só pendentes)
    @Transactional(readOnly = true)
    public List<PendenciaDTO> listarPendenciasDaSemana() {
        return listarPendencias(null, false);
    }

    // =============================================================
    // RESUMO RÁPIDO
    // =============================================================
    @Transactional(readOnly = true)
    public ResumoDTO resumoPendencias(LocalDate semanaInicio) {
        List<PendenciaDTO> todas = listarPendencias(semanaInicio, true);
        long ambas       = todas.stream().filter(p -> p.isRelatorioPendente() && p.isDiscipuladoPendente()).count();
        long relatorio   = todas.stream().filter(PendenciaDTO::isRelatorioPendente).count();
        long discipulado = todas.stream().filter(PendenciaDTO::isDiscipuladoPendente).count();
        long emDia       = todas.stream().filter(p -> !p.isRelatorioPendente() && !p.isDiscipuladoPendente()).count();
        return new ResumoDTO((long) todas.size(), ambas, relatorio, discipulado, emDia);
    }

    // =========================
    // DTO — Pendência por célula
    // =========================
    public static class PendenciaDTO {

        private final Long    idCelula;
        private final String  nomeCelula;
        private final String  nomeLider;
        private final String  bairro;
        private final boolean relatorioPendente;
        private final boolean discipuladoPendente;
        private final String  semanaInicio;
        private final String  semanaFim;

        public PendenciaDTO(Long idCelula, String nomeCelula, String nomeLider,
                            String bairro, boolean relatorioPendente,
                            boolean discipuladoPendente, String semanaInicio, String semanaFim) {
            this.idCelula            = idCelula;
            this.nomeCelula          = nomeCelula;
            this.nomeLider           = nomeLider;
            this.bairro              = bairro;
            this.relatorioPendente   = relatorioPendente;
            this.discipuladoPendente = discipuladoPendente;
            this.semanaInicio        = semanaInicio;
            this.semanaFim           = semanaFim;
        }

        public Long    getIdCelula()            { return idCelula;            }
        public String  getNomeCelula()          { return nomeCelula;          }
        public String  getNomeLider()           { return nomeLider;           }
        public String  getBairro()              { return bairro;              }
        public boolean isRelatorioPendente()    { return relatorioPendente;   }
        public boolean isDiscipuladoPendente()  { return discipuladoPendente; }
        public String  getSemanaInicio()        { return semanaInicio;        }
        public String  getSemanaFim()           { return semanaFim;           }
    }

    // =========================
    // DTO — Resumo de totais
    // =========================
    public static class ResumoDTO {

        private final long total;
        private final long ambasPendentes;
        private final long relatorioPendente;
        private final long discipuladoPendente;
        private final long emDia;

        public ResumoDTO(long total, long ambasPendentes,
                         long relatorioPendente, long discipuladoPendente, long emDia) {
            this.total               = total;
            this.ambasPendentes      = ambasPendentes;
            this.relatorioPendente   = relatorioPendente;
            this.discipuladoPendente = discipuladoPendente;
            this.emDia               = emDia;
        }

        public long getTotal()               { return total;               }
        public long getAmbasPendentes()      { return ambasPendentes;      }
        public long getRelatorioPendente()   { return relatorioPendente;   }
        public long getDiscipuladoPendente() { return discipuladoPendente; }
        public long getEmDia()               { return emDia;               }
    }
}