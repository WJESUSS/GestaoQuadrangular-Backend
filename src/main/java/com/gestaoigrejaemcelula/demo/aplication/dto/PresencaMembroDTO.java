package com.gestaoigrejaemcelula.demo.aplication.dto;

// DTO Auxiliar para a lista de membros dentro do card


// DTO Auxiliar para a lista de membros dentro do card


// DTO Auxiliar para a lista de membros dentro do card - COM JUSTIFICATIVAS
public record PresencaMembroDTO(
        Long id,
        String nomeMembro,
        boolean escolaBiblica,
        boolean quartaNoite,
        boolean quintaNoite,
        boolean domingoManha,
        boolean domingoNoite,
        // ✅ ADICIONADOS OS CAMPOS DE JUSTIFICATIVA:
        String justEscolaBiblica,
        String justQuartaNoite,
        String justQuintaNoite,
        String justDomingoManha,
        String justDomingoNoite,
        // Pontuação: quarta=2, quinta=2, domingo=4, escola bíblica=5
        int totalPontos
) {}