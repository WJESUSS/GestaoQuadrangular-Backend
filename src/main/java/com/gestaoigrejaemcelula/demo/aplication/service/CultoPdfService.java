package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.entity.Culto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CultoPdfService {

    private static final Font FONT_TITLE = new Font(Font.HELVETICA, 16, Font.BOLD);
    private static final Font FONT_SUBTITLE = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font FONT_NORMAL = new Font(Font.HELVETICA, 10);
    private static final Font FONT_HEADER = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Font FONT_HEADER_WHITE = new Font(Font.HELVETICA, 10, Font.BOLD, Color.white);
    private static final Font FONT_FOOTER = new Font(Font.HELVETICA, 8, Font.ITALIC);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── PDF individual ──────────────────────────────────────────────────

    public byte[] gerarPdfIndividual(Culto culto) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(doc, out);

        doc.open();
        addHeader(doc);
        doc.add(new Paragraph(" "));
        addTitulo(doc, "Registro de Culto");
        doc.add(new Paragraph(" "));

        Table table = new Table(2);
        table.setWidth(100);
        table.setBorderWidth(0.5f);
        table.setBorderColor(new Color(200, 200, 200));
        table.setPadding(5);
        table.setSpacing(3);

        addCampo(table, "Data", culto.getData() != null ? culto.getData().format(FMT) : "");
        addCampo(table, "Tipo", culto.getTipoCulto() != null ? culto.getTipoCulto().getDescricao() : "");
        addCampo(table, "Pregador", culto.getPregador());
        addCampo(table, "Texto Pregado", culto.getTextoPregado());
        addCampo(table, "Quant. Membros", String.valueOf(culto.getQuantidadeMembros()));
        addCampo(table, "Visitantes/Simpatizantes", String.valueOf(culto.getVisitantesSimpatizantes()));
        addCampo(table, "Total Crianças", String.valueOf(culto.getTotalCriancas()));
        addCampo(table, "Quant. Diáconos", String.valueOf(culto.getQuantidadeDiaconos()));
        addCampo(table, "TOTAL GERAL", String.valueOf(culto.getTotalGeral()));
        addCampo(table, "Campanha Ativa", Boolean.TRUE.equals(culto.getCampanhaAtiva()) ? "Sim" : "Não");
        if (culto.getNomeCampanha() != null) {
            addCampo(table, "Nome Campanha", culto.getNomeCampanha());
        }
        if (culto.getObservacoes() != null) {
            addCampo(table, "Observações", culto.getObservacoes());
        }
        addCampo(table, "Registrado por", culto.getRegistradoPor() != null
                ? culto.getRegistradoPor().getNome() : "");
        addCampo(table, "Criado em", culto.getCriadoEm() != null
                ? culto.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");

        doc.add(table);
        addFooter(doc);
        doc.close();

        return out.toByteArray();
    }

    // ── PDF geral ───────────────────────────────────────────────────────

    public byte[] gerarPdfGeral(List<Culto> cultos, LocalDate dataInicio, LocalDate dataFim) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        PdfWriter.getInstance(doc, out);

        doc.open();
        addHeader(doc);
        doc.add(new Paragraph(" "));
        addTitulo(doc, "Relatório Consolidado de Cultos");
        addSubtitulo(doc, "Período: " + dataInicio.format(FMT) + " a " + dataFim.format(FMT));
        addSubtitulo(doc, "Total de registros: " + cultos.size());
        doc.add(new Paragraph(" "));

        if (cultos.isEmpty()) {
            doc.add(new Paragraph("Nenhum registro encontrado para o período informado.", FONT_NORMAL));
        } else {
            Table table = new Table(8);
            table.setWidth(100);
            table.setBorderWidth(0.5f);
            table.setBorderColor(new Color(200, 200, 200));
            table.setPadding(4);
            table.setSpacing(2);

            addHeaderCell(table, "Data");
            addHeaderCell(table, "Tipo");
            addHeaderCell(table, "Pregador");
            addHeaderCell(table, "Membros");
            addHeaderCell(table, "Visit.");
            addHeaderCell(table, "Crianças");
            addHeaderCell(table, "Diáconos");
            addHeaderCell(table, "TOTAL");

            int totalMembros = 0, totalVisit = 0, totalCrianca = 0, totalDiaconos = 0, totalGeral = 0;

            for (Culto c : cultos) {
                addCell(table, c.getData() != null ? c.getData().format(FMT) : "");
                addCell(table, c.getTipoCulto() != null ? c.getTipoCulto().getDescricao() : "");
                addCell(table, c.getPregador());
                addCell(table, String.valueOf(c.getQuantidadeMembros()));
                addCell(table, String.valueOf(c.getVisitantesSimpatizantes()));
                addCell(table, String.valueOf(c.getTotalCriancas()));
                addCell(table, String.valueOf(c.getQuantidadeDiaconos()));
                addCell(table, String.valueOf(c.getTotalGeral()));

                totalMembros += c.getQuantidadeMembros();
                totalVisit += c.getVisitantesSimpatizantes();
                totalCrianca += c.getTotalCriancas();
                totalDiaconos += c.getQuantidadeDiaconos();
                totalGeral += c.getTotalGeral();
            }

            addHeaderCell(table, "TOTAL");
            addHeaderCell(table, "");
            addHeaderCell(table, "");
            addCellBold(table, String.valueOf(totalMembros));
            addCellBold(table, String.valueOf(totalVisit));
            addCellBold(table, String.valueOf(totalCrianca));
            addCellBold(table, String.valueOf(totalDiaconos));
            addCellBold(table, String.valueOf(totalGeral));

            doc.add(table);
        }

        addFooter(doc);
        doc.close();
        return out.toByteArray();
    }

    // ── HELPERS ─────────────────────────────────────────────────────────

    private void addHeader(Document doc) {
        Paragraph p = new Paragraph("Igreja do Evangelho Quadrangular", FONT_TITLE);
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);
    }

    private void addTitulo(Document doc, String texto) {
        Paragraph p = new Paragraph(texto, FONT_SUBTITLE);
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);
    }

    private void addSubtitulo(Document doc, String texto) {
        Paragraph p = new Paragraph(texto, FONT_NORMAL);
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);
    }

    private void addCampo(Table table, String label, String value) {
        Cell cellLabel = new Cell(new Paragraph(label, FONT_HEADER));
        cellLabel.setWidth(35);
        cellLabel.setBackgroundColor(new Color(240, 240, 240));
        table.addCell(cellLabel);

        Cell cellValue = new Cell(new Paragraph(value != null ? value : "", FONT_NORMAL));
        cellValue.setWidth(65);
        table.addCell(cellValue);
    }

    private void addHeaderCell(Table table, String text) {
        Cell cell = new Cell(new Paragraph(text, FONT_HEADER_WHITE));
        cell.setBackgroundColor(new Color(70, 130, 180));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addCell(Table table, String text) {
        Cell cell = new Cell(new Paragraph(text, FONT_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addCellBold(Table table, String text) {
        Cell cell = new Cell(new Paragraph(text, FONT_HEADER));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new Color(245, 245, 245));
        table.addCell(cell);
    }

    private void addFooter(Document doc) {
        doc.add(new Paragraph(" "));
        Paragraph footer = new Paragraph(
                "Gerado em " + LocalDate.now().format(FMT) + " - Sistema Gestão Quadrangular",
                FONT_FOOTER);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }
}
