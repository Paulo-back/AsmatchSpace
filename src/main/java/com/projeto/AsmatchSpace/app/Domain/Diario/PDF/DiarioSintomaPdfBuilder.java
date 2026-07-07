package com.projeto.AsmatchSpace.app.Domain.Diario.PDF;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.projeto.AsmatchSpace.app.Domain.Diario.DiarioSintoma;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class DiarioSintomaPdfBuilder {

    private static final DateTimeFormatter BR_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter BR_TIME = DateTimeFormatter.ofPattern("HH:mm");

    private static final Color HEADER_COLOR = new Color(52, 152, 219);
    private static final Color CINZA_CLARO  = new Color(245, 245, 245);
    private static final Color BORDA        = new Color(220, 220, 220);

    public static byte[] build(List<DiarioSintoma> dados, int meses) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // margens: esquerda, direita, topo, base
            Document doc = new Document(PageSize.A4, 40, 40, 40, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new RodapePagina());
            doc.open();

            /* ========= CABEÇALHO (logo + título lado a lado) ========= */
            Image logo = Image.getInstance(
                    Objects.requireNonNull(
                            DiarioSintomaPdfBuilder.class.getResource("/static/Logo.png")
                    )
            );
            logo.scaleToFit(55, 55);

            Font tituloFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(30, 30, 30));

            PdfPTable cabecalho = new PdfPTable(new float[]{1, 6});
            cabecalho.setWidthPercentage(100);
            cabecalho.setSpacingAfter(4f);

            PdfPCell logoCell = new PdfPCell(logo, false);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cabecalho.addCell(logoCell);

            PdfPCell tituloCell = new PdfPCell(
                    new Phrase("Relatório de Sintomas - Últimos " + meses + " meses", tituloFont));
            tituloCell.setBorder(Rectangle.NO_BORDER);
            tituloCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tituloCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cabecalho.addCell(tituloCell);

            doc.add(cabecalho);

            /* ========= PACIENTE ========= */
            String nomeCliente = dados.isEmpty()
                    ? "Paciente não identificado"
                    : dados.get(0).getCliente().getNome();

            Font pacienteFont = new Font(Font.HELVETICA, 11, Font.NORMAL, new Color(70, 70, 70));
            Paragraph paciente = new Paragraph("Cliente: " + nomeCliente, pacienteFont);
            paciente.setSpacingAfter(12f);
            doc.add(paciente);

            /* ========= TABELA ========= */
            Font tableFont  = new Font(Font.HELVETICA, 11);
            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);

            PdfPTable tabela = new PdfPTable(4);
            tabela.setWidthPercentage(100);
            tabela.setSpacingBefore(4f);
            tabela.setWidths(new float[]{2.2f, 2f, 3f, 6f});
            tabela.setHeaderRows(1); // repete o cabeçalho em cada página

            for (String h : new String[]{"Data", "Horário", "Intensidade", "Descrição"}) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(HEADER_COLOR);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(7);
                cell.setBorderColor(HEADER_COLOR);
                tabela.addCell(cell);
            }

            if (dados.isEmpty()) {
                PdfPCell vazio = new PdfPCell(
                        new Phrase("Nenhum sintoma registrado no período.", tableFont));
                vazio.setColspan(4);
                vazio.setHorizontalAlignment(Element.ALIGN_CENTER);
                vazio.setPadding(12);
                vazio.setBorderColor(BORDA);
                tabela.addCell(vazio);
            } else {
                boolean zebra = false;
                for (DiarioSintoma d : dados) {
                    Color fundo = zebra ? CINZA_CLARO : Color.WHITE;
                    zebra = !zebra;

                    tabela.addCell(cell(d.getData().format(BR_DATE), fundo, tableFont, Element.ALIGN_CENTER));
                    tabela.addCell(cell(d.getHorario().format(BR_TIME), fundo, tableFont, Element.ALIGN_CENTER));
                    tabela.addCell(cell(d.getIntensidade(), fundo, tableFont, Element.ALIGN_LEFT));
                    tabela.addCell(cell(d.getDescricao(), fundo, tableFont, Element.ALIGN_LEFT));
                }
            }

            doc.add(tabela);
            doc.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    private static PdfPCell cell(String texto, Color bg, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto == null ? "" : texto, font));
        c.setBackgroundColor(bg);
        c.setPadding(7);
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setBorderColor(BORDA);
        return c;
    }
}