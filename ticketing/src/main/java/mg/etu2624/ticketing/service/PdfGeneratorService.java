package mg.etu2624.ticketing.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import mg.etu2624.ticketing.model.Reservation;

@Service
public class PdfGeneratorService {

    // Couleurs et styles
    private static final Color PRIMARY_COLOR = new Color(15, 46, 83);  // Bleu foncé
    private static final Color ACCENT_COLOR = new Color(220, 53, 69); // Rouge vif
    private static final Color LIGHT_BG = new Color(245, 245, 245);   // Gris très clair
    
    public ResponseEntity<ByteArrayResource> generateReservationPdf(Reservation reservation) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 60, 36); // Marges
            PdfWriter.getInstance(document, out);
            document.open();

            // En-tête avec nom de l'application
            Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_COLOR);
            Paragraph appName = new Paragraph("AIRLINE TICKETING", logoFont);
            appName.setAlignment(Element.ALIGN_CENTER);
            appName.setSpacingAfter(5f);
            document.add(appName);

            // Ligne de séparation
            addEmptyLine(document, 1);
            addHorizontalLine(document, PRIMARY_COLOR);
            addEmptyLine(document, 1);

            // En-tête principal
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, PRIMARY_COLOR);
            Paragraph header = new Paragraph("TICKET DE RÉSERVATION", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(15f);
            document.add(header);

            // Section d'information importante
            PdfPTable infoHeader = new PdfPTable(2);
            infoHeader.setWidthPercentage(100);
            infoHeader.setSpacingBefore(10f);
            infoHeader.setSpacingAfter(15f);
            
            addInfoHeaderCell(infoHeader, "N° RÉSERVATION", reservation.getId().toString(), PRIMARY_COLOR);
            addInfoHeaderCell(infoHeader, "STATUT", "CONFIRMÉ", ACCENT_COLOR);
            
            document.add(infoHeader);

            // Tableau principal
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.35f, 0.65f});
            table.setSpacingBefore(15f);
            table.setSpacingAfter(25f);
            table.getDefaultCell().setPadding(8f);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

            addStyledRow(table, "Passager", reservation.getNomPassager(), labelFont, valueFont);
            addStyledRow(table, "Email", reservation.getEmailPassager(), labelFont, valueFont);
            addStyledRow(table, "N° Passeport", reservation.getPassportPassager(), labelFont, valueFont);
            addStyledRow(table, "Vol", reservation.getVol().getNumeroVol(), labelFont, valueFont);
            addStyledRow(table, "Classe", reservation.getClasseSiege().getNom(), labelFont, valueFont);
            addStyledRow(table, "Siège", reservation.getSiege().getNumeroSiege(), labelFont, valueFont);
            addStyledRow(table, "Date réservation", reservation.getDateReservation().format(formatter), labelFont, valueFont);
            addStyledRow(table, "Prix", String.format("%.2f €", reservation.getPrix()), labelFont, valueFont);
            
            if (reservation.getPromotion() != null) {
                addStyledRow(table, "Promotion", reservation.getPromotion().getDescription(), labelFont, valueFont);
                addStyledRow(table, "Réduction", String.format("-%.2f €", reservation.getReduction()), labelFont, valueFont);
                
                // Ligne de total
                Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, ACCENT_COLOR);
                addStyledRow(table, "TOTAL", String.format("%.2f €", reservation.getPrix().doubleValue() -  reservation.getReduction().doubleValue()), 
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY), 
                            totalFont);
            }

            document.add(table);

            // Section code-barres simulé
            addEmptyLine(document, 1);
            addHorizontalLine(document, Color.LIGHT_GRAY);
            
            Font barcodeFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16);
            Paragraph barcodeText = new Paragraph("✈️ " + reservation.getId() + " ✈️", barcodeFont);
            barcodeText.setAlignment(Element.ALIGN_CENTER);
            barcodeText.setSpacingAfter(5f);
            document.add(barcodeText);
            
            Font barcodeLabelFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY);
            Paragraph barcodeLabel = new Paragraph("ID de réservation", barcodeLabelFont);
            barcodeLabel.setAlignment(Element.ALIGN_CENTER);
            document.add(barcodeLabel);

            // Pied de page
            addEmptyLine(document, 3);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
            Paragraph footer = new Paragraph("Merci pour votre confiance • Votre ticket électronique • Service client: contact@airline.com", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=Ticket_" + reservation.getId() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(out.size())
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private void addInfoHeaderCell(PdfPTable table, String label, String value, Color color) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE);
        
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(10);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        Paragraph content = new Paragraph();
        content.add(new Phrase(label + "\n", labelFont));
        content.add(new Phrase(value, valueFont));
        cell.addElement(content);
        
        table.addCell(cell);
    }

    private void addStyledRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        // Cellule label
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(LIGHT_BG);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(8);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(labelCell);

        // Cellule valeur
        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(8);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(valueCell);
    }
    
    private void addHorizontalLine(Document document, Color color) throws Exception {
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100);
        separator.setSpacingAfter(5f);
        
        PdfPCell lineCell = new PdfPCell();
        lineCell.setFixedHeight(2f);
        lineCell.setBackgroundColor(color);
        lineCell.setBorder(Rectangle.NO_BORDER);
        separator.addCell(lineCell);
        
        document.add(separator);
    }
    
    private void addEmptyLine(Document document, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            document.add(new Paragraph(" "));
        }
    }
}