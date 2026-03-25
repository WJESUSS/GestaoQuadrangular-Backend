package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.AniversarianteDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;

import org.springframework.stereotype.Service;

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

            String mensagemCodificada =
                    URLEncoder.encode(mensagem, StandardCharsets.UTF_8);

            String telefoneLimpo =
                    m.getTelefone().replaceAll("[^0-9]", "");

            String link = "https://wa.me/" + telefoneLimpo +
                    "?text=" + mensagemCodificada;

            return new AniversarianteDTO(
                    m.getId(),
                    m.getNome(),
                    telefoneLimpo,
                    link
            );

        }).toList();
    }
}