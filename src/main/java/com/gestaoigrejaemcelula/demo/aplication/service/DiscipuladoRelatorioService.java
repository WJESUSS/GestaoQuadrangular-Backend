package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.AlertaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.PresencaMembroDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioDiscipuladoDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoRelatorio;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoRelatorioRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscipuladoRelatorioService {

    private final DiscipuladoRelatorioRepository repository;
    private final MembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository;
    private final CelulaRepository celulaRepository;

    // ✅ Helper para evitar NullPointerException em campos Boolean do banco
    private boolean safe(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    @Transactional
    public void salvarRelatorioSemanal(
            List<DiscipuladoRequestDTO> lista,
            LocalDate inicio,
            LocalDate fim
    ) {
        Usuario lider = usuarioLogado();

        for (DiscipuladoRequestDTO dto : lista) {

            boolean existe = repository.existsByMembroIdAndSemanaInicioAndSemanaFim(
                    dto.membroId(), inicio, fim
            );

            if (existe) {
                continue;
            }

            Long membroId = dto.membroId();

            Membro membro = membroRepository.findById(membroId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Membro não encontrado com ID: " + membroId));

            DiscipuladoRelatorio relatorio = new DiscipuladoRelatorio();
            relatorio.setSemanaInicio(inicio);
            relatorio.setSemanaFim(fim);
            relatorio.setMembro(membro);

            if (dto.celulaId() != null) {
                Celula celula = celulaRepository.findById(dto.celulaId())
                        .orElseThrow(() -> new RuntimeException("Célula não encontrada com ID: " + dto.celulaId()));
                System.out.println("DTO RECEBIDO -> Membro: " + dto.membroId() + " | Celula ID: " + dto.celulaId());
                relatorio.setCelula(celula);
            } else {
                relatorio.setCelula(membro.getCelula());
            }

            relatorio.setEscolaBiblica(dto.escolaBiblica());
            relatorio.setQuartaNoite(dto.quartaNoite());
            relatorio.setQuintaNoite(dto.quintaNoite());
            relatorio.setDomingoManha(dto.domingoManha());
            relatorio.setDomingoNoite(dto.domingoNoite());

            relatorio.setLider(lider);
            relatorio.setDataEnvio(LocalDateTime.now());

            relatorio.calcularPresenca();

            repository.save(relatorio);
        }
    }

    @Transactional(readOnly = true)
    public List<DiscipuladoRelatorio> listarSemana(LocalDate inicio, LocalDate fim) {
        return repository.findBySemanaInicioAndSemanaFim(inicio, fim);
    }

    private Usuario usuarioLogado() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado: " + email));
    }

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

    public List<AlertaDTO> obterAlertasCriticos() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(DayOfWeek.MONDAY);
        LocalDate fimSemana = hoje.with(DayOfWeek.SUNDAY);

        List<DiscipuladoRelatorio> relatorios =
                repository.findBySemanaInicioAndSemanaFim(inicioSemana, fimSemana);

        return relatorios.stream()
                .map(r -> {
                    int faltas = 0;

                    if (!safe(r.isEscolaBiblica())) faltas++;  // ✅ seguro contra null
                    if (!safe(r.isQuartaNoite()))   faltas++;  // ✅ seguro contra null
                    if (!safe(r.isQuintaNoite()))   faltas++;  // ✅ seguro contra null
                    if (!safe(r.isDomingoManha()))  faltas++;  // ✅ seguro contra null
                    if (!safe(r.isDomingoNoite()))  faltas++;  // ✅ seguro contra null

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
}