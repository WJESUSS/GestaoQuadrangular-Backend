package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.enums.EstadoCivil;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMembro;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoArrolamento;

import java.time.LocalDate;

public record MembroResponseDTO(

        // -------------------------------------------------------
        // DADOS BÁSICOS
        // -------------------------------------------------------
        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        String rg,
        EstadoCivil estadoCivil,
        StatusMembro status,
        LocalDate dataNascimento,
        LocalDate dataConversao,
        LocalDate dataBatismo,
        LocalDate dataCadastro,

        // -------------------------------------------------------
        // CÉLULA
        // -------------------------------------------------------
        Long celulaId,
        String nomeCelula,
        Long liderId,
        String nomeLider,

        // -------------------------------------------------------
        // FILIAÇÃO E NATURALIDADE
        // -------------------------------------------------------
        String nomeMae,
        String nomePai,
        String nomeConjuge,
        String naturalidade,

        // -------------------------------------------------------
        // ESCOLARIDADE E PROFISSÃO
        // -------------------------------------------------------
        String grauEscolaridade,
        String curso,
        String profissao,

        // -------------------------------------------------------
        // ENDEREÇO DETALHADO
        // -------------------------------------------------------
        String endereco,
        String numero,
        String bairro,
        String cidade,
        String cep,
        String uf,  // ✅ adicionado

        // -------------------------------------------------------
        // DADOS ESPIRITUAIS
        // -------------------------------------------------------
        Boolean pertenceOutraReligiao,
        String qualReligiao,
        Boolean batizadoNasAguas,
        LocalDate dataBatizadoNasAguas,
        String igrejaBatizadoNasAguas,
        Boolean batizadoEspiritoSanto,

        // -------------------------------------------------------
        // ARROLAMENTO
        // -------------------------------------------------------
        TipoArrolamento tipoArrolamento,
        String jurisdicaoArrolamento,
        String arroladoPor,

        // -------------------------------------------------------
        // OUTROS
        // -------------------------------------------------------
        String observacoes

) {
    public MembroResponseDTO(Membro m) {
        this(
                m.getId(),
                m.getNome(),
                m.getEmail(),
                m.getTelefone(),
                m.getCpf(),
                m.getRg(),
                m.getEstadoCivil(),
                m.getStatus(),
                m.getDataNascimento(),
                m.getDataConversao(),
                m.getDataBatismo(),
                m.getDataCadastro(),
                m.getCelula() != null ? m.getCelula().getId() : null,
                m.getCelula() != null ? m.getCelula().getNome() : "Sem célula",
                m.getCelula() != null && m.getCelula().getLider() != null
                        ? m.getCelula().getLider().getId() : null,
                m.getCelula() != null && m.getCelula().getLider() != null
                        ? m.getCelula().getLider().getNome() : "Sem líder",
                m.getNomeMae(),
                m.getNomePai(),
                m.getNomeConjuge(),
                m.getNaturalidade(),
                m.getGrauEscolaridade(),
                m.getCurso(),
                m.getProfissao(),
                m.getEndereco(),
                m.getNumero(),
                m.getBairro(),
                m.getCidade(),
                m.getCep(),
                m.getUf(),
                m.getPertenceOutraReligiao(),
                m.getQualReligiao(),
                m.getBatizadoNasAguas(),
                m.getDataBatizadoNasAguas(),
                m.getIgrejaBatizadoNasAguas(),
                m.getBatizadoEspiritoSanto(),
                m.getTipoArrolamento(),
                m.getJurisdicaoArrolamento(),
                m.getArroladoPor(),
                m.getObservacoes()
        );
    }
}