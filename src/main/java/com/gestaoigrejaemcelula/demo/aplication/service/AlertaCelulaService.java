package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.CelulaAlertaDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Relatorio;
import com.gestaoigrejaemcelula.demo.domain.repository.RelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaCelulaService {

    private final RelatorioRepository repository;

    public List<CelulaAlertaDTO> gerarAlertas() {

        LocalDate hoje = LocalDate.now();

        LocalDate inicioAtual = hoje.minusDays(30);
        LocalDate inicioAnterior = hoje.minusDays(60);

        List<Relatorio> atual =
                repository.findByDataBetween(inicioAtual, hoje);

        List<Relatorio> anterior =
                repository.findByDataBetween(inicioAnterior, inicioAtual);

        Map<Long, Double> mediaAtual = calcularMediaPorCelula(atual);
        Map<Long, Double> mediaAnterior = calcularMediaPorCelula(anterior);

        List<CelulaAlertaDTO> alertas = new ArrayList<>();

        for (Long celulaId : mediaAtual.keySet()) {

            double atualMedia = mediaAtual.getOrDefault(celulaId, 0.0);
            double anteriorMedia = mediaAnterior.getOrDefault(celulaId, 0.0);

            if (atualMedia < anteriorMedia || atualMedia < 8) {

                String nivel = calcularNivel(atualMedia, anteriorMedia);

                Relatorio ref = atual.stream()
                        .filter(r -> r.getCelula().getId().equals(celulaId))
                        .findFirst()
                        .orElse(null);

                if (ref != null) {
                    alertas.add(
                            CelulaAlertaDTO.builder()
                                    .celulaId(celulaId)
                                    .nomeCelula(ref.getCelula().getNome())
                                    .lider(ref.getCelula().getLider().getNome())
                                    .mediaAtual(atualMedia)
                                    .mediaAnterior(anteriorMedia)
                                    .nivel(nivel)
                                    .build()
                    );
                }
            }
        }

        return alertas;
    }

    private Map<Long, Double> calcularMediaPorCelula(List<Relatorio> lista) {
        return lista.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCelula().getId(),
                        Collectors.averagingInt(Relatorio::getTotalPresentes)
                ));
    }

    private String calcularNivel(double atual, double anterior) {

        if (atual < 6) return "ALTO";
        if (atual < anterior) return "MEDIO";
        return "BAIXO";
    }
}
