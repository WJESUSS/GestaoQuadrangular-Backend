package com.gestaoigrejaemcelula.demo.aplication.service;


import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.HistoricoCelula;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.CelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.HistoricoCelulaRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class VinculoCelulaService {

    private final MembroRepository membroRepository;
    private final CelulaRepository celulaRepository;
    private final HistoricoCelulaRepository historicoRepository;

    public VinculoCelulaService(MembroRepository membroRepository,
                                CelulaRepository celulaRepository,
                                HistoricoCelulaRepository historicoRepository) {
        this.membroRepository = membroRepository;
        this.celulaRepository = celulaRepository;
        this.historicoRepository = historicoRepository;
    }

    // 🔹 VINCULAR
    public void vincular(Long membroId, Long celulaId) {

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        Celula celula = celulaRepository.findById(celulaId)
                .orElseThrow(() -> new RuntimeException("Célula não encontrada"));

        // encerra vínculo anterior, se existir
        HistoricoCelula atual =
                historicoRepository.findByMembroIdAndDataSaidaIsNull(membroId);

        if (atual != null) {
            atual.setDataSaida(LocalDate.now());
            historicoRepository.save(atual);
        }

        // cria novo histórico
        HistoricoCelula novo = new HistoricoCelula();
        novo.setMembro(membro);
        novo.setCelula(celula);
        novo.setDataEntrada(LocalDate.now());

        historicoRepository.save(novo);

        // atualiza célula atual do membro
        membro.setCelula(celula);
        membroRepository.save(membro);
    }

    // 🔹 REMOVER DA CÉLULA
    public void remover(Long membroId) {

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        HistoricoCelula atual =
                historicoRepository.findByMembroIdAndDataSaidaIsNull(membroId);

        if (atual != null) {
            atual.setDataSaida(LocalDate.now());
            historicoRepository.save(atual);
        }

        membro.setCelula(null);
        membroRepository.save(membro);
    }
}
