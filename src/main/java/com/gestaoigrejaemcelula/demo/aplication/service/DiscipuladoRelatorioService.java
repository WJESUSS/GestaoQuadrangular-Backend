package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.PresencaMembroDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioDiscipuladoDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoRelatorio;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository; // <--- IMPORTANTE
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoRelatorioRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CelulaRepository celulaRepository; // <--- ADICIONADO: Necessário para buscar a célula

    /**
     * Salva o relatório semanal de discipulado
     */
    @Transactional
    public void salvarRelatorioSemanal(
            List<DiscipuladoRequestDTO> lista,
            LocalDate inicio,
            LocalDate fim
    ) {
        Usuario lider = usuarioLogado();

        for (DiscipuladoRequestDTO dto : lista) {

            // Evita duplicar relatório da mesma semana para o mesmo membro
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

            // =================================================================================
            // CORREÇÃO CRÍTICA AQUI:
            // Busca a célula pelo ID que veio do Frontend e atribui ao relatório.
            // =================================================================================
            if (dto.celulaId() != null) {
                Celula celula = celulaRepository.findById(dto.celulaId())
                        .orElseThrow(() -> new RuntimeException("Célula não encontrada com ID: " + dto.celulaId()));
                System.out.println("DTO RECEBIDO -> Membro: " + dto.membroId() + " | Celula ID: " + dto.celulaId());

                relatorio.setCelula(celula); // <--- AGORA O ID SERÁ SALVO NO BANCO

            } else {
                // Fallback: Se o front não mandou (erro), tenta pegar do cadastro do membro
                relatorio.setCelula(membro.getCelula());
            }
            // =================================================================================

            relatorio.setEscolaBiblica(dto.escolaBiblica());
            relatorio.setCultoSemana(dto.cultoSemana());
            relatorio.setDomingoManha(dto.domingoManha());
            relatorio.setDomingoNoite(dto.domingoNoite());

            relatorio.setLider(lider);
            relatorio.setDataEnvio(LocalDateTime.now());

            relatorio.calcularPresenca();

            repository.save(relatorio);
        }
    }

    /**
     * Lista relatórios de uma semana específica (para pastor/secretaria)
     */
    @Transactional(readOnly = true)
    public List<DiscipuladoRelatorio> listarSemana(
            LocalDate inicio,
            LocalDate fim
    ) {
        return repository.findBySemanaInicioAndSemanaFim(inicio, fim);
    }

    /**
     * Usuário autenticado (líder que está enviando o relatório)
     */
    private Usuario usuarioLogado() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado: " + email));
    }

    /**
     * Lista todos os relatórios agrupados para visualização da Secretaria / Pastor
     */
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
                                    r.isEscolaBiblica(),
                                    r.isCultoSemana(),
                                    r.isDomingoManha(),
                                    r.isDomingoNoite()
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
}