package com.gestaoigrejaemcelula.demo.aplication.dto;




/**
 * DTO para resumir a participação nos encontros.
 * Baseado na ficha de inscrição dos participantes.
 */
public class RelatorioEncontroResumoDTO {

    private long totalParticipantes;    // Total de inscritos
    private long totalAceitaramJesus;   // Quantos aceitaram Jesus
    private long totalDesejamBatismo;   // Quantos desejam batismo
    private long totalDesejamDiscipulado; // Quantos desejam discipulado
    private long totalDesejamParticiparCelula; // Quantos desejam participar de célula

    // --- Getters e Setters ---
    public long getTotalParticipantes() {
        return totalParticipantes;
    }

    public void setTotalParticipantes(long totalParticipantes) {
        this.totalParticipantes = totalParticipantes;
    }

    public long getTotalAceitaramJesus() {
        return totalAceitaramJesus;
    }

    public void setTotalAceitaramJesus(long totalAceitaramJesus) {
        this.totalAceitaramJesus = totalAceitaramJesus;
    }

    public long getTotalDesejamBatismo() {
        return totalDesejamBatismo;
    }

    public void setTotalDesejamBatismo(long totalDesejamBatismo) {
        this.totalDesejamBatismo = totalDesejamBatismo;
    }

    public long getTotalDesejamDiscipulado() {
        return totalDesejamDiscipulado;
    }

    public void setTotalDesejamDiscipulado(long totalDesejamDiscipulado) {
        this.totalDesejamDiscipulado = totalDesejamDiscipulado;
    }

    public long getTotalDesejamParticiparCelula() {
        return totalDesejamParticiparCelula;
    }

    public void setTotalDesejamParticiparCelula(long totalDesejamParticiparCelula) {
        this.totalDesejamParticiparCelula = totalDesejamParticiparCelula;
    }
}
