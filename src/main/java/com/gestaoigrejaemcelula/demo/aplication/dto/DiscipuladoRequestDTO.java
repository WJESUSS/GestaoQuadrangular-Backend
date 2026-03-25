package com.gestaoigrejaemcelula.demo.aplication.dto;

public record DiscipuladoRequestDTO(
        Long membroId,
        Long celulaId, // <--- ADICIONE AQUI! O Jackson vai preencher isso automaticamente.
        boolean escolaBiblica,
        boolean cultoSemana,
        boolean domingoManha,
        boolean domingoNoite
) {
    // Não precisa escrever nada aqui dentro.
    // O Java já cria automaticamente o método public Long celulaId()
}