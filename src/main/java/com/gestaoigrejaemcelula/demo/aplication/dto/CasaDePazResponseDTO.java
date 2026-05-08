package com.gestaoigrejaemcelula.demo.domain.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.CasaDePaz;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusCasaDePaz;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CasaDePazResponseDTO {

    private Long id;
    private String nome;
    private String nomeAnfitriao;
    private String endereco;
    private String telefoneContato;
    private LocalDate dataInicio;
    private int encontrosRestantes;
    private int encontrosRealizados;
    private StatusCasaDePaz status;

    private Long celulaId;
    private String celulaNome;

    private Long liderId;
    private String liderNome;

    private Long auxiliarId;
    private String auxiliarNome;

    // ── ADICIONADO: lista de visitantes para o front poder exibir checkboxes de decisão ──
    private List<VisitanteSimples> visitantes;

    public static class VisitanteSimples {
        private Long id;
        private String nome;

        public VisitanteSimples(Long id, String nome) {
            this.id   = id;
            this.nome = nome;
        }

        public Long getId()   { return id; }
        public String getNome() { return nome; }
    }

    public static CasaDePazResponseDTO de(CasaDePaz casa) {
        CasaDePazResponseDTO dto = new CasaDePazResponseDTO();
        dto.setId(casa.getId());
        dto.setNome(casa.getNome());
        dto.setNomeAnfitriao(casa.getNomeAnfitriao());
        dto.setEndereco(casa.getEndereco());
        dto.setTelefoneContato(casa.getTelefoneContato());
        dto.setDataInicio(casa.getDataInicio());
        dto.setEncontrosRestantes(casa.getEncontrosRestantes());

        // encontrosRealizados = 5 - restantes (ou use campo direto se existir na entidade)
        dto.setEncontrosRealizados(5 - casa.getEncontrosRestantes());

        dto.setStatus(casa.getStatus());
        dto.setCelulaId(casa.getCelula().getId());
        dto.setCelulaNome(casa.getCelula().getNome());
        dto.setLiderId(casa.getLider().getId());
        dto.setLiderNome(casa.getLider().getNome());
        dto.setAuxiliarId(casa.getAuxiliar().getId());
        dto.setAuxiliarNome(casa.getAuxiliar().getNome());

        // ── ADICIONADO: mapeia visitantes da entidade ──
        if (casa.getVisitantes() != null) {
            dto.setVisitantes(
                    casa.getVisitantes().stream()
                            .map(v -> new VisitanteSimples(v.getId(), v.getNome()))
                            .collect(Collectors.toList())
            );
        } else {
            dto.setVisitantes(List.of());
        }

        return dto;
    }

    // ── Getters e Setters ──
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeAnfitriao() { return nomeAnfitriao; }
    public void setNomeAnfitriao(String nomeAnfitriao) { this.nomeAnfitriao = nomeAnfitriao; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public int getEncontrosRestantes() { return encontrosRestantes; }
    public void setEncontrosRestantes(int encontrosRestantes) { this.encontrosRestantes = encontrosRestantes; }

    public int getEncontrosRealizados() { return encontrosRealizados; }
    public void setEncontrosRealizados(int encontrosRealizados) { this.encontrosRealizados = encontrosRealizados; }

    public StatusCasaDePaz getStatus() { return status; }
    public void setStatus(StatusCasaDePaz status) { this.status = status; }

    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }

    public String getCelulaNome() { return celulaNome; }
    public void setCelulaNome(String celulaNome) { this.celulaNome = celulaNome; }

    public Long getLiderId() { return liderId; }
    public void setLiderId(Long liderId) { this.liderId = liderId; }

    public String getLiderNome() { return liderNome; }
    public void setLiderNome(String liderNome) { this.liderNome = liderNome; }

    public Long getAuxiliarId() { return auxiliarId; }
    public void setAuxiliarId(Long auxiliarId) { this.auxiliarId = auxiliarId; }

    public String getAuxiliarNome() { return auxiliarNome; }
    public void setAuxiliarNome(String auxiliarNome) { this.auxiliarNome = auxiliarNome; }

    public List<VisitanteSimples> getVisitantes() { return visitantes; }
    public void setVisitantes(List<VisitanteSimples> visitantes) { this.visitantes = visitantes; }
}