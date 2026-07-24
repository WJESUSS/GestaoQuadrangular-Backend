package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.AniversarianteDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class AniversarioService {

    private static final Logger log = LoggerFactory.getLogger(AniversarioService.class);

    private static final ZoneId ZONE_BAHIA = ZoneId.of("America/Bahia");

    private final MembroRepository membroRepository;

    public AniversarioService(MembroRepository membroRepository) {
        this.membroRepository = membroRepository;
    }

    @Cacheable(value = "aniversariantes",
            key = "'dia-' + T(java.time.LocalDate).now(T(java.time.ZoneId).of('America/Bahia')).toString()")
    @Transactional(readOnly = true)
    public List<AniversarianteDTO> listarAniversariantesDoDia() {
        LocalDate hoje = LocalDate.now(ZONE_BAHIA);

        log.info("Procurando aniversariantes para: {}", hoje);

        List<Membro> membros = membroRepository.findAniversariantesDoDia(
                hoje.getMonthValue(),
                hoje.getDayOfMonth()
        );

        log.info("Encontrados: {} aniversariantes", membros.size());

        return membros.stream().map(this::toDTO).toList();
    }

    @Cacheable(value = "aniversariantes",
            key = "'semana-' + T(java.time.LocalDate).now(T(java.time.ZoneId).of('America/Bahia')).toString()")
    @Transactional(readOnly = true)
    public List<AniversarianteDTO> listarAniversariantesDaSemana() {

        LocalDate hoje = LocalDate.now(ZONE_BAHIA);

        List<Integer> diasMes = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            LocalDate d = hoje.plusDays(i);
            int dia = d.getMonthValue() * 100 + d.getDayOfMonth();
            diasMes.add(dia);
        }

        log.info("Procurando aniversariantes da semana: {}", diasMes);

        List<Membro> membros = membroRepository.findAniversariantesPorDiasMes(diasMes);

        log.info("Encontrados: {} aniversariantes", membros.size());

        return membros.stream().map(this::toDTO).toList();
    }

    private AniversarianteDTO toDTO(Membro m) {
        String mensagem = """
🎂 Paz seja contigo minha ovelhinha 🙏! Feliz Aniversário %s!

Que Deus abençoe sua vida,
lhe conceda saúde, paz e prosperidade.

Com carinho,
Pastores Renato e Jaci Soares 🙏 🤍""".formatted(m.getNome());

        String telefoneLimpo = m.getTelefone().replaceAll("[^0-9]", "");

        if (!telefoneLimpo.startsWith("55")) {
            telefoneLimpo = "55" + telefoneLimpo;
        }

        return new AniversarianteDTO(
                m.getId(),
                m.getNome(),
                m.getTelefone(),
                mensagem
        );
    }

    @Transactional(readOnly = true)
    public List<AniversarianteDTO> listarAniversariantesDoDiaPorCelula(Long celulaId) {
        LocalDate hoje = LocalDate.now(ZONE_BAHIA);
        List<Membro> membros = membroRepository.findAniversariantesDoDiaPorCelula(
                celulaId,
                hoje.getMonthValue(),
                hoje.getDayOfMonth()
        );
        return membros.stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<AniversarianteDTO> listarAniversariantesSemanaPoeCelula(Long celulaId) {
        LocalDate hoje = LocalDate.now(ZONE_BAHIA);
        List<Integer> diasMes = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            LocalDate d = hoje.plusDays(i);
            diasMes.add(d.getMonthValue() * 100 + d.getDayOfMonth());
        }
        List<Membro> membros = membroRepository.findAniversariantesSemanaPoeCelula(celulaId, diasMes);
        return membros.stream().map(this::toDTO).toList();
    }
}