package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.FichaEncontroRequestDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioEncontroResumoDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioPorConvidadorDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.FichaEncontro;
import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.Sexo;
import com.gestaoigrejaemcelula.demo.domain.repository.FichaEncontroRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RelatorioEncontroService {

    private final FichaEncontroRepository repository;

    public RelatorioEncontroService(FichaEncontroRepository repository) {
        this.repository = repository;
    }

    public RelatorioEncontroResumoDTO gerarResumo() {
        RelatorioEncontroResumoDTO dto = new RelatorioEncontroResumoDTO();

        dto.setTotalParticipantes(repository.countByIdIsNotNull());


        return dto;
    }

    public List<RelatorioPorConvidadorDTO> relatorioPorConvidador() {
        return repository.totalPorConvidador()
                .stream()
                .map(obj -> new RelatorioPorConvidadorDTO(
                        (String) obj[0],
                        (Long) obj[1]
                ))
                .toList();
    }
    public FichaEncontro converterDtoParaEntidade(FichaEncontroRequestDTO dto) {
        FichaEncontro ficha = new FichaEncontro();
        ficha.setNomeConvidado(dto.getNome());
        ficha.setTelefone(dto.getTelefone());
        ficha.setDataNascimento(dto.getDataNascimento());
        ficha.setSexo(dto.getSexo().equalsIgnoreCase("M") ? Sexo.MASCULINO : Sexo.FEMININO);
        ficha.setEstadoCivil(EstadoCivil.valueOf(dto.getEstadoCivil().toUpperCase()));
        ficha.setPeso(dto.getPeso());
        ficha.setAltura(dto.getAltura());

        ficha.setLiderResponsavel(dto.getNomeLiderCelula());
        // … preencher os outros campos conforme necessário
        return ficha;
    }


    public List<FichaEncontro> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            // Exemplo: se não enviar data, busca os últimos 30 dias ou retorna erro
            throw new IllegalArgumentException("As datas de início e fim devem ser informadas.");
        }
        return repository.findByDataInicioBetween(dataInicio, dataFim);
    }
}
