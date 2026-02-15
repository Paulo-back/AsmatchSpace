package com.projeto.AsmatchSpace.app.Domain.Diario.PDF;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import com.projeto.AsmatchSpace.app.Domain.Diario.DiarioSintoma;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class DiarioSintomaPdfBuilder {


    public static byte[] build(
            List<DiarioSintoma> dados,
            int meses) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new RodapePagina()); // rodapé
            doc.open();

            /* ========= LOGO ========= */
            Image logo = Image.getInstance(
                    Objects.requireNonNull(
                            DiarioSintomaPdfBuilder.class
                                    .getResource("/static/Logo.png")
                    )
            );
            logo.scaleToFit(70, 70);
            logo.setAlignment(Image.ALIGN_LEFT);
            doc.add(logo);

            /* ========= TÍTULO ========= */
            Font tituloFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph titulo = new Paragraph(
                    "Relatório de Sintomas - Últimos " + meses + " meses",
                    tituloFont
            );
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            String nomeCliente =
                    dados.isEmpty()
                            ? "Paciente não identificado"
                            : dados.get(0).getCliente().getNome();

            doc.add(new Paragraph(
                    "Paciente: " + nomeCliente + "\n\n"
            ));

            /* ========= TABELA ========= */

            Font tableFont = new Font(Font.HELVETICA, 12);

            PdfPTable tabela = new PdfPTable(4);
            tabela.setWidthPercentage(100);
            tabela.setSpacingBefore(15f);
            tabela.setWidths(new float[]{2, 2, 3, 6});

//            PdfPTable tabela = new PdfPTable(4);
//            tabela.setWidthPercentage(100);
//            tabela.setSpacingBefore(10f);

            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Color headerColor = new Color(52, 152, 219);

            String[] headers = {
                    "Data", "Horário", "Intensidade", "Descrição"
            };

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                tabela.addCell(cell);
            }

            boolean zebra = false;
            Color cinzaClaro = new Color(245, 245, 245);

            for (DiarioSintoma d : dados) {

                Color fundo = zebra ? cinzaClaro : Color.WHITE;
                zebra = !zebra;

//                tabela.addCell(cell(d.getData(), fundo));
//                tabela.addCell(cell(d.getHorario(), fundo));
//                tabela.addCell(cell(d.getIntensidade(), fundo));
//                tabela.addCell(cell(d.getDescricao(), fundo));

//                tabela.addCell(cell(d.getData(), fundo, tableFont));
                tabela.addCell(cell(
                        formatarDataBR(d.getData()),
                        fundo,
                        tableFont
                ));
                tabela.addCell(cell(d.getHorario(), fundo, tableFont));
                tabela.addCell(cell(d.getIntensidade(), fundo, tableFont));
                tabela.addCell(cell(d.getDescricao(), fundo, tableFont));
            }

            doc.add(tabela);
            doc.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao gerar PDF", e);
        }
    }

    /* ========= CELULA PADRÃO ========= */
    private static PdfPCell cell(String texto, Color bg, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setBackgroundColor(bg);
        c.setPadding(8);
        return c;
    }

    private static String formatarDataBR(String dataISO) {
        try {
            LocalDate date = LocalDate.parse(dataISO);
            DateTimeFormatter br =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(br);
        } catch (Exception e) {
            return dataISO; // fallback
        }
    }
}