package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.CasaDePazPdfRequestDTO;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class CasaDePazPdfService {

    // ── Paleta IEQ ────────────────────────────────────────────────────────────
    private static final Color BG        = new DeviceRgb(17,  10,  13 );
    private static final Color CARD_BG   = new DeviceRgb(30,  18,  22 );
    private static final Color RED       = new DeviceRgb(200, 16,  46 );
    private static final Color RED_DARK  = new DeviceRgb(139, 11,  31 );
    private static final Color YELLOW    = new DeviceRgb(253, 184, 19 );
    private static final Color TEAL      = new DeviceRgb(93,  202, 165);
    private static final Color BLUE_FADE = new DeviceRgb(122, 171, 244);
    private static final Color OFF_WHITE = new DeviceRgb(245, 240, 232);
    private static final Color MUTED     = new DeviceRgb(140, 130, 120);
    private static final Color CARD_LINE = new DeviceRgb(60,  25,  35 );

    private static final DateTimeFormatter FMT_IN  = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FMT_OUT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─────────────────────────────────────────────────────────────────────────
    public byte[] gerar(CasaDePazPdfRequestDTO req) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter   writer  = new PdfWriter(baos);
        PdfDocument pdf     = new PdfDocument(writer);
        Document    doc     = new Document(pdf, PageSize.A4);
        doc.setMargins(20, 20, 20, 20);

        PdfFont bold   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        List<CasaDePazPdfRequestDTO.CasaDTO> casas = req.getCasas() != null ? req.getCasas() : List.of();

        // ── Fundo escuro em cada página (via evento) ──────────────────────────
        pdf.addEventHandler(com.itextpdf.kernel.events.PdfDocumentEvent.START_PAGE,
                event -> {
                    PdfDocument pdfDoc = ((com.itextpdf.kernel.events.PdfDocumentEvent) event).getDocument();
                    PdfCanvas canvas = new PdfCanvas(
                            pdfDoc.getLastPage().newContentStreamBefore(),
                            pdfDoc.getLastPage().getResources(),
                            pdfDoc
                    );
                    canvas.setFillColor(BG)
                            .rectangle(0, 0, PageSize.A4.getWidth(), PageSize.A4.getHeight())
                            .fill()
                            .release();
                }
        );

        // ── Barra vermelha no topo ────────────────────────────────────────────
        Table topBar = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setHeight(4)
                .setBackgroundColor(RED)
                .setBorder(Border.NO_BORDER)
                .setMarginBottom(10);
        topBar.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
        doc.add(topBar);

        // ── Cabeçalho ─────────────────────────────────────────────────────────
        doc.add(new Paragraph("IEQ PITUAÇU · PAINEL PASTORAL")
                .setFont(bold).setFontSize(7).setFontColor(RED)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(2));

        doc.add(new Paragraph("RELATÓRIO DE CASAS DE PAZ")
                .setFont(bold).setFontSize(16).setFontColor(OFF_WHITE)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(2));

        doc.add(new Paragraph(req.getCelulaName() != null ? req.getCelulaName().toUpperCase() : "")
                .setFont(bold).setFontSize(12).setFontColor(YELLOW)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(2));

        String hoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
                new java.util.Locale("pt", "BR")));
        doc.add(new Paragraph("Gerado em " + hoje)
                .setFont(normal).setFontSize(8).setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(8));

        // Linha separadora
        doc.add(linhaSep());

        // ── KPIs ──────────────────────────────────────────────────────────────
        int totalDec  = casas.stream().mapToInt(CasaDePazPdfRequestDTO.CasaDTO::totalDecisoes).sum();
        int concl     = (int) casas.stream().filter(c -> "CONCLUIDA".equals(c.getStatus())).count();
        int andamento = (int) casas.stream().filter(c -> "EM_ANDAMENTO".equals(c.getStatus())).count();
        int cancelado = (int) casas.stream().filter(c -> "CANCELADA".equals(c.getStatus())).count();
        int totalVis  = casas.stream().mapToInt(CasaDePazPdfRequestDTO.CasaDTO::getTotalVisitantes).sum();

        float[] kpiCols = {1f, 1f, 1f, 1f, 1f, 1f};
        Table kpiTable = new Table(UnitValue.createPercentArray(kpiCols))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(12)
                .setBorder(Border.NO_BORDER);

        addKpi(kpiTable, bold, normal, "TOTAL CASAS",   String.valueOf(casas.size()), "casas",        RED);
        addKpi(kpiTable, bold, normal, "CONCLUÍDAS",    String.valueOf(concl),        "encerradas",   TEAL);
        addKpi(kpiTable, bold, normal, "ANDAMENTO",     String.valueOf(andamento),    "ativas",       YELLOW);
        addKpi(kpiTable, bold, normal, "CANCELADAS",    String.valueOf(cancelado),    "interrompidas",new DeviceRgb(232,41,74));
        addKpi(kpiTable, bold, normal, "VISITANTES",    String.valueOf(totalVis),     "registrados",  BLUE_FADE);
        addKpi(kpiTable, bold, normal, "DECISÕES",      String.valueOf(totalDec),     "total",        YELLOW);
        doc.add(kpiTable);

        // ── Tabela-resumo de todas as casas ───────────────────────────────────
        doc.add(new Paragraph("LISTA DE CASAS (" + casas.size() + ")")
                .setFont(bold).setFontSize(8).setFontColor(RED).setMarginBottom(4));

        float[] cols = {0.04f, 0.22f, 0.18f, 0.13f, 0.1f, 0.1f, 0.1f, 0.13f};
        Table tabela = new Table(UnitValue.createPercentArray(cols))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(14)
                .setBorder(Border.NO_BORDER);

        // Cabeçalho da tabela
        String[] headers = {"#", "Nome da Casa", "Líder", "Status", "Encontros", "Visitantes", "Decisões", "Início"};
        for (String h : headers) {
            tabela.addHeaderCell(
                    new Cell().setBackgroundColor(RED_DARK)
                            .setBorder(Border.NO_BORDER)
                            .add(new Paragraph(h).setFont(bold).setFontSize(7).setFontColor(OFF_WHITE))
            );
        }

        // Linhas
        for (int i = 0; i < casas.size(); i++) {
            CasaDePazPdfRequestDTO.CasaDTO c = casas.get(i);
            Color rowBg = (i % 2 == 0) ? CARD_BG : new DeviceRgb(22, 13, 17);
            addRow(tabela, normal, bold, rowBg, i + 1, c);
        }
        doc.add(tabela);

        // ── Cards detalhados por casa ─────────────────────────────────────────
        doc.add(new Paragraph("DETALHES POR CASA")
                .setFont(bold).setFontSize(8).setFontColor(RED).setMarginBottom(6));

        for (int i = 0; i < casas.size(); i++) {
            doc.add(buildCasaCard(casas.get(i), i + 1, bold, normal));
        }

        // ── Rodapé ────────────────────────────────────────────────────────────
        doc.add(linhaSep());
        doc.add(new Paragraph("© IEQ PITUAÇU · SISTEMA SEGURO · " + LocalDate.now().getYear())
                .setFont(normal).setFontSize(7).setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(4));

        doc.close();
        return baos.toByteArray();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Table linhaSep() {
        Table t = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setHeight(1)
                .setBorder(Border.NO_BORDER)
                .setMarginBottom(8).setMarginTop(2);
        t.addCell(new Cell().setBackgroundColor(RED).setBorder(Border.NO_BORDER)
                .add(new Paragraph("")));
        return t;
    }

    private void addKpi(Table t, PdfFont bold, PdfFont normal,
                        String label, String val, String sub, Color accentColor) {
        Cell cell = new Cell()
                .setBackgroundColor(CARD_BG)
                .setBorder(new SolidBorder(CARD_LINE, 0.5f))
                .setPadding(8)
                .setBorderLeft(new SolidBorder(accentColor, 3));

        cell.add(new Paragraph(label).setFont(bold).setFontSize(6.5f).setFontColor(MUTED).setMarginBottom(4));
        cell.add(new Paragraph(val).setFont(bold).setFontSize(16).setFontColor(OFF_WHITE).setMarginBottom(2));
        cell.add(new Paragraph(sub).setFont(normal).setFontSize(8).setFontColor(MUTED));
        t.addCell(cell);
    }

    private void addRow(Table t, PdfFont normal, PdfFont bold, Color bg, int idx, CasaDePazPdfRequestDTO.CasaDTO c) {
        String[] statusLabel = statusInfo(c.getStatus());
        String[] values = {
                String.valueOf(idx),
                nvl(c.getNome()),
                nvl(c.getNomeLider()),
                statusLabel[0],
                c.getEncontrosRealizados() + "/" + c.totalEncontros(),
                String.valueOf(c.getTotalVisitantes()),
                String.valueOf(c.totalDecisoes()),
                fmtData(c.getDataInicio())
        };

        for (int col = 0; col < values.length; col++) {
            Color txtColor = (col == 3) ? statusColor(c.getStatus()) : OFF_WHITE;
            t.addCell(
                    new Cell().setBackgroundColor(bg)
                            .setBorder(new SolidBorder(CARD_LINE, 0.2f))
                            .add(new Paragraph(values[col])
                                    .setFont(col == 3 ? bold : normal)
                                    .setFontSize(7.5f)
                                    .setFontColor(txtColor)
                                    .setTextAlignment(col == 0 || col >= 4 ? TextAlignment.CENTER : TextAlignment.LEFT))
            );
        }
    }

    private Table buildCasaCard(CasaDePazPdfRequestDTO.CasaDTO c, int idx, PdfFont bold, PdfFont normal) {
        // Container do card
        Table card = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(CARD_LINE, 0.5f))
                .setMarginBottom(8)
                .setKeepTogether(true);

        Cell outer = new Cell()
                .setBackgroundColor(CARD_BG)
                .setBorder(Border.NO_BORDER)
                .setBorderLeft(new SolidBorder(RED, 3))
                .setPadding(10);

        // Nome + status
        Table header = new Table(UnitValue.createPercentArray(new float[]{1, 0.3f}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER).setMarginBottom(4);

        header.addCell(new Cell().setBorder(Border.NO_BORDER).add(
                new Paragraph(idx + ". " + nvl(c.getNome()))
                        .setFont(bold).setFontSize(10).setFontColor(OFF_WHITE)));

        String stLabel = statusInfo(c.getStatus())[0];
        Color  stColor = statusColor(c.getStatus());
        header.addCell(new Cell().setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT).add(
                        new Paragraph("● " + stLabel)
                                .setFont(bold).setFontSize(7).setFontColor(stColor)));
        outer.add(header);

        // Endereço / data
        if (c.getEndereco() != null && !c.getEndereco().isBlank()) {
            outer.add(new Paragraph(c.getEndereco())
                    .setFont(normal).setFontSize(8).setFontColor(MUTED).setMarginBottom(2));
        }
        if (c.getDataInicio() != null && !c.getDataInicio().isBlank()) {
            outer.add(new Paragraph("Início: " + fmtData(c.getDataInicio()))
                    .setFont(normal).setFontSize(7.5f).setFontColor(MUTED).setMarginBottom(4));
        }

        // Barra de progresso (simulada com texto + underline)
        int tot = c.totalEncontros();
        int rea = c.getEncontrosRealizados();
        int pct = tot > 0 ? (rea * 100 / tot) : 0;
        outer.add(new Paragraph("ENCONTROS: " + rea + "/" + tot + "  ·  " + c.getEncontrosRestantes() + " restante(s)  [" + pct + "%]")
                .setFont(bold).setFontSize(7).setFontColor(MUTED).setMarginBottom(4));

        // Mini stats
        float[] sc = {1f, 1f, 1f, 1f};
        Table stats = new Table(UnitValue.createPercentArray(sc))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER).setMarginBottom(6);

        addMiniStat(stats, bold, normal, "REALIZADOS", String.valueOf(rea),                      "encontros", OFF_WHITE);
        addMiniStat(stats, bold, normal, "RESTANTES",  String.valueOf(c.getEncontrosRestantes()), "encontros", OFF_WHITE);
        addMiniStat(stats, bold, normal, "VISITANTES", String.valueOf(c.getTotalVisitantes()),    "únicos",    OFF_WHITE);
        addMiniStat(stats, bold, normal, "DECISÕES",   String.valueOf(c.totalDecisoes()),         "total",     YELLOW);
        outer.add(stats);

        // Equipe
        if (c.getNomeLider() != null || c.getNomeAuxiliar() != null) {
            outer.add(linhaMenor());
            outer.add(new Paragraph("EQUIPE RESPONSÁVEL")
                    .setFont(bold).setFontSize(6.5f).setFontColor(MUTED).setMarginBottom(3));

            Table equipe = new Table(UnitValue.createPercentArray(new float[]{1f, 1f}))
                    .setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER);
            if (c.getNomeLider() != null) {
                equipe.addCell(new Cell().setBorder(Border.NO_BORDER).add(
                        new Paragraph("LÍDER: " + c.getNomeLider())
                                .setFont(normal).setFontSize(8).setFontColor(OFF_WHITE)));
            }
            if (c.getNomeAuxiliar() != null) {
                equipe.addCell(new Cell().setBorder(Border.NO_BORDER).add(
                        new Paragraph("AUXILIAR: " + c.getNomeAuxiliar())
                                .setFont(normal).setFontSize(8).setFontColor(OFF_WHITE)));
            }
            outer.add(equipe);
        }

        // Visitantes
        outer.add(linhaMenor());
        List<CasaDePazPdfRequestDTO.VisitanteDTO> visitantes = c.getVisitantes() != null ? c.getVisitantes() : List.of();
        outer.add(new Paragraph("VISITANTES (" + (visitantes.isEmpty() ? c.getTotalVisitantes() : visitantes.size()) + ")")
                .setFont(bold).setFontSize(6.5f).setFontColor(MUTED).setMarginBottom(3));

        if (!visitantes.isEmpty()) {
            // 4 colunas de visitantes
            Table visTable = new Table(UnitValue.createPercentArray(new float[]{1f, 1f, 1f, 1f}))
                    .setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER);
            for (CasaDePazPdfRequestDTO.VisitanteDTO v : visitantes) {
                boolean temDec = v.getDecisao() != null && !v.getDecisao().isBlank();
                String txt = nvl(v.getNome()) + (temDec ? " ★" : "");
                visTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                        .setBackgroundColor(temDec ? new DeviceRgb(253,184,19) : CARD_BG)
                        .setPadding(2)
                        .add(new Paragraph(txt).setFont(normal).setFontSize(7)
                                .setFontColor(temDec ? YELLOW : new DeviceRgb(200, 185, 170))));
            }
            outer.add(visTable);
        } else if (c.getTotalVisitantes() > 0) {
            outer.add(new Paragraph(c.getTotalVisitantes() + " visitante(s) registrado(s)")
                    .setFont(normal).setFontSize(7).setFontColor(MUTED));
        } else {
            outer.add(new Paragraph("Nenhum visitante registrado")
                    .setFont(normal).setFontSize(7).setFontColor(MUTED));
        }

        // Decisões
        if (c.totalDecisoes() > 0) {
            outer.add(linhaMenor());
            outer.add(new Paragraph("DECISÕES REGISTRADAS")
                    .setFont(bold).setFontSize(6.5f).setFontColor(MUTED).setMarginBottom(3));

            StringBuilder dec = new StringBuilder();
            if (c.getTotalAceitouJesus()  > 0) dec.append("♥ ").append(c.getTotalAceitouJesus()).append(" ACEITAÇÃO(ÕES)   ");
            if (c.getTotalReconciliacao() > 0) dec.append("★ ").append(c.getTotalReconciliacao()).append(" RECONCILIAÇÃO(ÕES)   ");
            if (c.getTotalDesejoBatismo() > 0) dec.append("◆ ").append(c.getTotalDesejoBatismo()).append(" BATISMO(S)");

            outer.add(new Paragraph(dec.toString().trim())
                    .setFont(bold).setFontSize(7.5f).setFontColor(YELLOW));
        }

        card.addCell(outer);
        return card;
    }

    private void addMiniStat(Table t, PdfFont bold, PdfFont normal,
                             String label, String val, String sub, Color valColor) {
        Cell c = new Cell()
                .setBackgroundColor(new DeviceRgb(0, 0, 0))
                .setBorder(new SolidBorder(new DeviceRgb(200,16,46), 0.5f))
                .setPadding(6);
        c.add(new Paragraph(label).setFont(bold).setFontSize(6).setFontColor(MUTED).setMarginBottom(3));
        c.add(new Paragraph(val).setFont(bold).setFontSize(13).setFontColor(valColor).setMarginBottom(1));
        c.add(new Paragraph(sub).setFont(normal).setFontSize(7).setFontColor(MUTED));
        t.addCell(c);
    }

    private Table linhaMenor() {
        Table t = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER)
                .setMarginTop(5).setMarginBottom(5);
        t.addCell(new Cell().setBackgroundColor(CARD_LINE).setHeight(0.5f)
                .setBorder(Border.NO_BORDER).add(new Paragraph("")));
        return t;
    }

    private String[] statusInfo(String status) {
        if (status == null) return new String[]{"—"};
        return switch (status) {
            case "EM_ANDAMENTO" -> new String[]{"Em Andamento"};
            case "CONCLUIDA"    -> new String[]{"Concluída"};
            case "CANCELADA"    -> new String[]{"Cancelada"};
            default             -> new String[]{status};
        };
    }

    private Color statusColor(String status) {
        if (status == null) return MUTED;
        return switch (status) {
            case "EM_ANDAMENTO" -> TEAL;
            case "CONCLUIDA"    -> BLUE_FADE;
            case "CANCELADA"    -> new DeviceRgb(232, 41, 74);
            default             -> MUTED;
        };
    }

    private String fmtData(String iso) {
        if (iso == null || iso.isBlank()) return "—";
        try { return LocalDate.parse(iso, FMT_IN).format(FMT_OUT); }
        catch (DateTimeParseException e) { return iso; }
    }

    private String nvl(String s) { return s != null ? s : ""; }
}