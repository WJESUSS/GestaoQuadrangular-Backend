package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.AniversarianteDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class AniversarioService {

    // ✅ Fuso fixo de Salvador/Bahia
    private static final ZoneId ZONE_BAHIA = ZoneId.of("America/Bahia");

    private final MembroRepository membroRepository;

    public AniversarioService(MembroRepository membroRepository) {
        this.membroRepository = membroRepository;
    }

    @Cacheable(value = "aniversariantes",
            key = "'dia-' + T(java.time.LocalDate).now(T(java.time.ZoneId).of('America/Bahia')).toString()")
    @Transactional(readOnly = true)
    public List<AniversarianteDTO> listarAniversariantesDoDia() {

        // ✅ Sempre pega a data correta de Salvador
        LocalDate hoje = LocalDate.now(ZONE_BAHIA);

        List<Membro> membros = membroRepository.findAniversariantesDoDia(
                hoje.getMonthValue(),
                hoje.getDayOfMonth()
        );

        return membros.stream().map(m -> toDTO(m)).toList();
    }

    @Cacheable(value = "aniversariantes",
            key = "'semana-' + T(java.time.LocalDate).now(T(java.time.ZoneId).of('America/Bahia')).toString()")
    @Transactional(readOnly = true)
    public List<AniversarianteDTO> listarAniversariantesDaSemana() {

        // ✅ Sempre pega a data correta de Salvador
        LocalDate hoje = LocalDate.now(ZONE_BAHIA);

        List<Integer> diasMes = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            LocalDate d = hoje.plusDays(i);
            diasMes.add(d.getMonthValue() * 100 + d.getDayOfMonth());
        }

        List<Membro> membros = membroRepository.findAniversariantesPorDiasMes(diasMes);

        return membros.stream().map(m -> toDTO(m)).toList();
    }

    // ✅ Método auxiliar para evitar duplicação
    private AniversarianteDTO toDTO(Membro m) {
        String mensagem = """
                🎂 Feliz Aniversário %s!

                Que Deus abençoe sua vida,
                lhe conceda saúde, paz e prosperidade.

                Com carinho,
                Pastor e Pastora 🙏
                """.formatted(m.getNome());

        String mensagemCodificada = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
        String telefoneLimpo = m.getTelefone().replaceAll("[^0-9]", "");

        // ✅ Garante DDI 55 no número
        if (!telefoneLimpo.startsWith("55")) {
            telefoneLimpo = "55" + telefoneLimpo;
        }

        String link = "https://wa.me/" + telefoneLimpo + "?text=" + mensagemCodificada;

        return new AniversarianteDTO(m.getId(), m.getNome(), telefoneLimpo, link);
    }
}