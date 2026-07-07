package com.projeto.AsmatchSpace.app.Domain.Diario.PDF;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class RodapePagina extends PdfPageEventHelper {

    Font font = new Font(Font.HELVETICA, 8, Font.NORMAL, new java.awt.Color(120, 120, 120));

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Rectangle page = document.getPageSize();
        float centerX = (page.getLeft() + page.getRight()) / 2;

        ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_CENTER,
                new Phrase("Página " + writer.getPageNumber(), font),
                centerX,
                page.getBottom() + 20,
                0
        );
    }
}