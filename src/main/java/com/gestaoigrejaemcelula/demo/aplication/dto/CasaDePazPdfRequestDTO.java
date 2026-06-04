package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CasaDePazPdfRequestDTO {
    @NotBlank private String celulaName;
    @NotNull @Valid private List<CasaDTO> casas;

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String getCelulaName() { return celulaName; }
    public void setCelulaName(String celulaName) { this.celulaName = celulaName; }

    public List<CasaDTO> getCasas() { return casas; }
    public void setCasas(List<CasaDTO> casas) { this.casas = casas; }

    // ─────────────────────────────────────────────────────────────────────────

    public static class CasaDTO {
        private Long   id;
        private String nome;
        private String endereco;
        private String status;
        private String dataInicio;
        private String nomeLider;
        private String nomeAuxiliar;
        private int    encontrosRealizados;
        private int    encontrosRestantes;
        private int    totalAceitouJesus;
        private int    totalReconciliacao;
        private int    totalDesejoBatismo;
        private int    totalVisitantes;
        private List<VisitanteDTO> visitantes;

        // Getters / Setters
        public Long   getId()                    { return id; }
        public void   setId(Long id)             { this.id = id; }
        public String getNome()                  { return nome; }
        public void   setNome(String nome)       { this.nome = nome; }
        public String getEndereco()              { return endereco; }
        public void   setEndereco(String e)      { this.endereco = e; }
        public String getStatus()                { return status; }
        public void   setStatus(String s)        { this.status = s; }
        public String getDataInicio()            { return dataInicio; }
        public void   setDataInicio(String d)    { this.dataInicio = d; }
        public String getNomeLider()             { return nomeLider; }
        public void   setNomeLider(String n)     { this.nomeLider = n; }
        public String getNomeAuxiliar()          { return nomeAuxiliar; }
        public void   setNomeAuxiliar(String n)  { this.nomeAuxiliar = n; }
        public int    getEncontrosRealizados()   { return encontrosRealizados; }
        public void   setEncontrosRealizados(int v) { this.encontrosRealizados = v; }
        public int    getEncontrosRestantes()    { return encontrosRestantes; }
        public void   setEncontrosRestantes(int v)  { this.encontrosRestantes = v; }
        public int    getTotalAceitouJesus()     { return totalAceitouJesus; }
        public void   setTotalAceitouJesus(int v)   { this.totalAceitouJesus = v; }
        public int    getTotalReconciliacao()    { return totalReconciliacao; }
        public void   setTotalReconciliacao(int v)  { this.totalReconciliacao = v; }
        public int    getTotalDesejoBatismo()    { return totalDesejoBatismo; }
        public void   setTotalDesejoBatismo(int v)  { this.totalDesejoBatismo = v; }
        public int    getTotalVisitantes()       { return totalVisitantes; }
        public void   setTotalVisitantes(int v)  { this.totalVisitantes = v; }
        public List<VisitanteDTO> getVisitantes() { return visitantes; }
        public void   setVisitantes(List<VisitanteDTO> v) { this.visitantes = v; }

        public int totalDecisoes() {
            return totalAceitouJesus + totalReconciliacao + totalDesejoBatismo;
        }
        public int totalEncontros() {
            return encontrosRealizados + encontrosRestantes;
        }
    }

    public static class VisitanteDTO {
        private String nome;
        private String decisao; // ACEITACAO | RECONCILIACAO | BATISMO | null

        public String getNome()    { return nome; }
        public void   setNome(String nome) { this.nome = nome; }
        public String getDecisao() { return decisao; }
        public void   setDecisao(String decisao) { this.decisao = decisao; }
    }
}