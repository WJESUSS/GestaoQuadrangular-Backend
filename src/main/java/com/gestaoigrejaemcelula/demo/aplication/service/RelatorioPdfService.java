package com.gestaoigrejaemcelula.demo.aplication.service;



import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioResponseDTO;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

// keep your other imports:
import com.gestaoigrejaemcelula.demo.aplication.dto.RelatorioResponseDTO;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.util.List;


@Service
public class RelatorioPdfService {

    public byte[] gerarPdf(List<RelatorioResponseDTO> relatorios) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("RELATÓRIO DE CÉLULAS - IGREJA QUADRANGULAR")
                    .setBold()
                    .setFontSize(16));

            document.add(new Paragraph(" "));

            // AJUSTE: Array de 5 posições para 5 colunas
            // Pesos: Célula(3), Líder(3), Membros(1.5), Visitantes(1.5), Total(1)
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 1.5f, 1.5f, 1}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Estilizando o cabeçalho
            table.addHeaderCell(new Paragraph("Célula").setBold());
            table.addHeaderCell(new Paragraph("Nome do Líder").setBold());
            table.addHeaderCell(new Paragraph("Membros").setBold());
            table.addHeaderCell(new Paragraph("Visitantes").setBold());
            table.addHeaderCell(new Paragraph("Total").setBold());

            for (RelatorioResponseDTO r : relatorios) {
                int membros = (r.getMembrosPresentes() != null) ? r.getMembrosPresentes().size() : 0;
                int visitantes = (r.getVisitantesPresentes() != null) ? r.getVisitantesPresentes().size() : 0;
                int total = membros + visitantes;

                table.addCell(r.getNomeCelula() != null ? r.getNomeCelula() : "---");
                table.addCell(r.getNomeLider() != null ? r.getNomeLider() : "-----");
                table.addCell(String.valueOf(membros));
                table.addCell(String.valueOf(visitantes));
                table.addCell(String.valueOf(total));
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }
}