package com.gestaoigrejaemcelula.demo.aplication.dto;



import java.time.LocalDate;
import java.util.List;

public record RelatorioDiscipuladoDTO(
        Long id,
         Long celulaId,
        String nomeCelula,
        String nomeLider,
        LocalDate dataInicio,
        LocalDate dataFim,
        List<PresencaMembroDTO> presencas
) {}

