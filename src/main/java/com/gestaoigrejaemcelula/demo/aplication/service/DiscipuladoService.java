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
import java.util.ArrayList;
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
        int ano;
        int mes;

        try {
            // Verifica se a string não é nula e contém o separador esperado
            if (mesRef != null && mesRef.contains("-")) {
                String[] partes = mesRef.split("-");
                ano = Integer.parseInt(partes[0]);
                mes = Integer.parseInt(partes[1]);
            } else {
                // FALLBACK: Se o frontend enviar algo errado como "abril",
                // usamos o mês e ano atuais como padrão para não quebrar o sistema.
                java.time.LocalDate hoje = java.time.LocalDate.now();
                ano = hoje.getYear();
                mes = hoje.getMonthValue();
                // Ajustamos o mesRef para o formato esperado pelo repositório (ex: "2026-04")
                mesRef = String.format("%d-%02d", ano, mes);

                System.out.println("Aviso: mesRef inválido recebido (" + mesRef + "). Usando data atual.");
            }

            // 2. Busca os dados brutos (Object[]) do repositório
            List<Object[]> resultados = relatorioRepo.buscarAlertasPastor(mes, ano, mesRef);

            // 3. Converte a lista de Object[] para Lista de AlertaDTO
            if (resultados == null) return new java.util.ArrayList<>();

            return resultados.stream().map(obj -> new AlertaDTO(
                    ((Number) obj[0]).longValue(), // id
                    (String) obj[1],                // nome
                    (String) obj[2],                // telefone
                    (String) obj[3],                // nomeCelula
                    ((Number) obj[4]).intValue()    // totalFaltas
            )).toList();

        } catch (Exception e) {
            // Log do erro para depuração, mas retorna lista vazia para o frontend não travar
            System.err.println("Erro crítico ao processar alertas: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
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
                rel.isQuartaNoite(),
                rel.isQuintaNoite(),
                rel.isDomingoManha(),
                rel.isDomingoNoite()
        )).collect(Collectors.toList());
    }

;

    public List<AlertaDTO> obterAlertasCriticosPorMes(String mesRef) {
        try {
            // 1. Extrai Ano e Mês da String "2026-04"
            String[] partes = mesRef.split("-");
            int ano = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);

            // 2. Busca a lista de membros que faltaram no banco
            // Usamos List<Object[]> porque queries nativas retornam arrays de colunas
            List<Object[]> resultados = relatorioRepo.buscarAlertasDetalhados(mes, ano, mesRef);

            // 3. Mapeia os resultados do Banco para o seu DTO
            return resultados.stream()
                    .map(obj -> new AlertaDTO(
                            ((Number) obj[0]).longValue(), // ID do membro
                            (String) obj[1],                // Nome
                            (String) obj[2],                // Telefone
                            (String) obj[3],                // Nome da Célula
                            ((Number) obj[4]).intValue()    // Total de faltas no mês
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Log de erro para ajudar na depuração se a data vier errada
            System.err.println("Erro ao buscar alertas críticos para " + mesRef + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}