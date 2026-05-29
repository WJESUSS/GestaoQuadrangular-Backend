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
import java.util.List;

@Service
public class AniversarioService {

    private final MembroRepository membroRepository;

    public AniversarioService(MembroRepository membroRepository) {
        this.membroRepository = membroRepository;
    }

    // Chave = "dia-05-09" — muda automaticamente todo dia
    @Cacheable(value = "aniversariantes", key = "'dia-' + T(java.time.LocalDate).now().toString()")
    @Transactional(readOnly = true)
    public List<AniversarianteDTO> listarAniversariantesDoDia() {
        LocalDate hoje = LocalDate.now();

        List<Membro> membros = membroRepository.findAniversariantesDoDia(
                hoje.getMonthValue(),
                hoje.getDayOfMonth()
        );

        return membros.stream().map(m -> {
            String mensagem = """
                    🎉 Feliz Aniversário %s!
                    
                    Que Deus abençoe sua vida,
                    lhe conceda saúde, paz e prosperidade.
                    
                    Com carinho,
                    Pastor e Pastora ❤️
                    """.formatted(m.getNome());

            String mensagemCodificada = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
            String telefoneLimpo = m.getTelefone().replaceAll("[^0-9]", "");
            String link = "https://wa.me/" + telefoneLimpo + "?text=" + mensagemCodificada;

            return new AniversarianteDTO(m.getId(), m.getNome(), telefoneLimpo, link);
        }).toList();
    }

    @Cacheable(value = "aniversariantes", key = "'semana-' + T(java.time.LocalDate).now().toString()")
    @Transactional(readOnly = true)
    public List<AniversarianteDTO> listarAniversariantesDaSemana() {
        LocalDate hoje = LocalDate.now();

        // Gera lista de pares mês*100+dia para os próximos 7 dias
        // Ex: 29/05 → 529, 30/05 → 530, 01/06 → 601...
        List<Integer> diasMes = new java.util.ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            LocalDate d = hoje.plusDays(i);
            diasMes.add(d.getMonthValue() * 100 + d.getDayOfMonth());
        }

        List<Membro> membros = membroRepository.findAniversariantesPorDiasMes(diasMes);

        return membros.stream().map(m -> {
            String mensagem = """
                    🎉 Feliz Aniversário %s!
                    
                    Que Deus abençoe sua vida,
                    lhe conceda saúde, paz e prosperidade.
                    
                    Com carinho,
                    Pastor e Pastora ❤️
                    """.formatted(m.getNome());
            String mensagemCodificada = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
            String telefoneLimpo = m.getTelefone().replaceAll("[^0-9]", "");
            String link = "https://wa.me/" + telefoneLimpo + "?text=" + mensagemCodificada;
            return new AniversarianteDTO(m.getId(), m.getNome(), telefoneLimpo, link);
        }).toList();
    }
}