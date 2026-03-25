package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.AlertaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.DiscipuladoRelatorioResponseDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoAcompanhamento;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.repository.AcompanhamentoRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.DiscipuladoRelatorioRepository;
import com.gestaoigrejaemcelula.demo.domain.repository.MembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscipuladoService {

    @Autowired
    private DiscipuladoRelatorioRepository relatorioRepo;
    @Autowired
    private AcompanhamentoRepository accRepo;
    @Autowired
    private MembroRepository membroRepo;

    public List<AlertaDTO> buscarAlertas(String mesRef) {
        // 1. Extrai ano e mês da String "2026-01"
        String[] partes = mesRef.split("-");
        int ano = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);

        // 2. Busca os dados brutos (Object[]) do repositório
        List<Object[]> resultados = relatorioRepo.buscarAlertasPastor(mes, ano, mesRef);

        // 3. Converte a lista de Object[] para Lista de AlertaDTO
        return resultados.stream().map(obj -> new AlertaDTO(
                ((Number) obj[0]).longValue(), // id
                (String) obj[1],               // nome
                (String) obj[2],               // telefone
                (String) obj[3],               // nomeCelula
                ((Number) obj[4]).intValue()   // totalFaltas
        )).toList();
    }

    @Transactional
    public void registrarCuidado(Long membroId, String mesRef) {
        if (accRepo.existsByMembroIdAndMesReferencia(membroId, mesRef)) return;

        Membro m = membroRepo.findById(membroId).orElseThrow();
        DiscipuladoAcompanhamento da = new DiscipuladoAcompanhamento();
        da.setMembro(m);
        da.setMesReferencia(mesRef);
        da.setDataAcao(LocalDate.now());
        accRepo.save(da);
    }

    // Dentro do seu Service ou Controller
    public List<DiscipuladoRelatorioResponseDTO> listarTodosParaSecretaria() {
        // Usando o método que criamos no Repository com JOIN FETCH
        return relatorioRepo.findAllComDetalhes().stream().map(rel -> new DiscipuladoRelatorioResponseDTO(
                rel.getId(),
                // BUSCA O NOME DA CÉLULA NA ENTIDADE CELULA
                rel.getCelula() != null ? rel.getCelula().getNome() : "ID da Celula: " + (rel.getCelula() != null ? rel.getCelula().getId() : "Nulo no Banco"),
                rel.getLider() != null ? rel.getLider().getNome() : "Líder não informado",
                rel.getMembro() != null ? rel.getMembro().getNome() : "Membro não informado",
                rel.getSemanaInicio(),
                rel.getSemanaFim(),
                rel.isEscolaBiblica(),
                rel.isCultoSemana(),
                rel.isDomingoManha(),
                rel.isDomingoNoite()
        )).collect(Collectors.toList());
    }
}