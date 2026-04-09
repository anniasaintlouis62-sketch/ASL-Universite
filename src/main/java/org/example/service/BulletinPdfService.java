package org.example.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.example.dto.BulletinData;
import org.example.dto.BulletinLine;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class BulletinPdfService {

    public byte[] createPdf(BulletinData bulletin) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("Bulletin Universitaire"));
            document.add(new Paragraph("Etudiant: " + bulletin.studentName() + " (" + bulletin.studentId() + ")"));
            document.add(new Paragraph("Annee academique: " + bulletin.academicYear()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.addCell("Matiere");
            table.addCell("Professeur");
            table.addCell("Coefficient");
            table.addCell("Note");

            for (BulletinLine line : bulletin.lines()) {
                table.addCell(line.subjectName());
                table.addCell(line.professorName());
                table.addCell(String.valueOf(line.coefficient()));
                table.addCell(String.valueOf(line.grade()));
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Moyenne: " + String.format("%.2f", bulletin.average())));
            document.add(new Paragraph("Mention: " + bulletin.mention()));
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur de generation PDF", e);
        }
    }
}
