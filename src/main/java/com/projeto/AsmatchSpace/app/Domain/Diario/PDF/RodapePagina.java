package com.projeto.AsmatchSpace.app.Domain.Diario.PDF;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class RodapePagina extends PdfPageEventHelper {

    Font font = new Font(Font.HELVETICA, 8);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Phrase rodape = new Phrase(
                "Página " + writer.getPageNumber(),
                font
        );

        ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_CENTER,
                rodape,
                300, 20, 0
        );
    }
}
