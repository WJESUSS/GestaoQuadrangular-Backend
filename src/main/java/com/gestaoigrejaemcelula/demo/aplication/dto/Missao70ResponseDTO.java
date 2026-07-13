package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.entity.Missao70;
import com.gestaoigrejaemcelula.demo.domain.enums.MotivoCancelamentoMissao70;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class Missao70ResponseDTO {

    private Long id;
    private String nome;
    private String nomeAnfitriao;
    private String endereco;
    private String telefoneContato;
    private LocalDate dataInicio;
    private LocalTime horario;
    private int encontrosRestantes;
    private int encontrosRealizados;
    private StatusMissao70 status;

    private MotivoCancelamentoMissao70 motivoCancelamento;
    private String motivoCancelamentoDescricao;
    private String observacaoCancelamento;

    private Long celulaId;
    private String celulaNome;

    private Long liderId;
    private String liderNome;

    private Long auxiliarId;
    private String auxiliarNome;

    private Long terceiroMembroId;
    private String terceiroMembroNome;

    private List<VisitanteSimples> visitantes;

    public static class VisitanteSimples {
        private Long id;
        private String nome;
        public VisitanteSimples(Long id, String nome) { this.id = id; this.nome = nome; }
        public Long getId() { return id; }
        public String getNome() { return nome; }
    }

    public static Missao70ResponseDTO de(Missao70 missao) {
        Missao70ResponseDTO dto = new Missao70ResponseDTO();
        dto.setId(missao.getId());
        dto.setNome(missao.getNome());
        dto.setNomeAnfitriao(missao.getNomeAnfitriao());
        dto.setEndereco(missao.getEndereco());
        dto.setTelefoneContato(missao.getTelefoneContato());
        dto.setDataInicio(missao.getDataInicio());
        dto.setHorario(missao.getHorario());
        dto.setEncontrosRestantes(missao.getEncontrosRestantes());
        dto.setEncontrosRealizados(4 - missao.getEncontrosRestantes());
        dto.setStatus(missao.getStatus());

        dto.setMotivoCancelamento(missao.getMotivoCancelamento());
        dto.setMotivoCancelamentoDescricao(
                missao.getMotivoCancelamento() != null ? missao.getMotivoCancelamento().getDescricao() : null
        );
        dto.setObservacaoCancelamento(missao.getObservacaoCancelamento());

        if (missao.getCelula() != null) {
            dto.setCelulaId(missao.getCelula().getId());
            dto.setCelulaNome(missao.getCelula().getNome());
        }

        if (missao.getLider() != null) {
            dto.setLiderId(missao.getLider().getId());
            dto.setLiderNome(missao.getLider().getNome());
        }

        if (missao.getAuxiliar() != null) {
            dto.setAuxiliarId(missao.getAuxiliar().getId());
            dto.setAuxiliarNome(missao.getAuxiliar().getNome());
        }

        if (missao.getTerceiroMembro() != null) {
            dto.setTerceiroMembroId(missao.getTerceiroMembro().getId());
            dto.setTerceiroMembroNome(missao.getTerceiroMembro().getNome());
        }

        dto.setVisitantes(
                missao.getVisitantes() != null
                        ? missao.getVisitantes().stream()
                        .map(v -> new VisitanteSimples(v.getId(), v.getNome()))
                        .collect(Collectors.toList())
                        : List.of()
        );

        return dto;
    }

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
    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }
    public int getEncontrosRestantes() { return encontrosRestantes; }
    public void setEncontrosRestantes(int encontrosRestantes) { this.encontrosRestantes = encontrosRestantes; }
    public int getEncontrosRealizados() { return encontrosRealizados; }
    public void setEncontrosRealizados(int encontrosRealizados) { this.encontrosRealizados = encontrosRealizados; }
    public StatusMissao70 getStatus() { return status; }
    public void setStatus(StatusMissao70 status) { this.status = status; }
    public MotivoCancelamentoMissao70 getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(MotivoCancelamentoMissao70 motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }
    public String getMotivoCancelamentoDescricao() { return motivoCancelamentoDescricao; }
    public void setMotivoCancelamentoDescricao(String motivoCancelamentoDescricao) { this.motivoCancelamentoDescricao = motivoCancelamentoDescricao; }
    public String getObservacaoCancelamento() { return observacaoCancelamento; }
    public void setObservacaoCancelamento(String observacaoCancelamento) { this.observacaoCancelamento = observacaoCancelamento; }
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
    public Long getTerceiroMembroId() { return terceiroMembroId; }
    public void setTerceiroMembroId(Long terceiroMembroId) { this.terceiroMembroId = terceiroMembroId; }
    public String getTerceiroMembroNome() { return terceiroMembroNome; }
    public void setTerceiroMembroNome(String terceiroMembroNome) { this.terceiroMembroNome = terceiroMembroNome; }
    public List<VisitanteSimples> getVisitantes() { return visitantes; }
    public void setVisitantes(List<VisitanteSimples> visitantes) { this.visitantes = visitantes; }
}