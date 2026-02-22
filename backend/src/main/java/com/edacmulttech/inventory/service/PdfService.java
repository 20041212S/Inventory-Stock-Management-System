package com.edacmulttech.inventory.service;

import com.edacmulttech.inventory.dto.StockDTO;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class PdfService {

    public byte[] exportStocksToPdf(List<StockDTO> stocks) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            
            // Title
            Paragraph title = new Paragraph("Stock Inventory Report")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);
            
            // Create table
            float[] columnWidths = {2, 3, 2, 2, 2, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Header row
            String[] headers = {"Part Number", "Part Name", "Stock Qty", "Bin", "Rack", "Last Updated"};
            for (String header : headers) {
                Cell cell = new Cell()
                        .add(new Paragraph(header).setFont(boldFont))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5);
                table.addHeaderCell(cell);
            }
            
            // Data rows
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (StockDTO stock : stocks) {
                table.addCell(new Cell().add(new Paragraph(stock.getPartNumber() != null ? stock.getPartNumber() : "").setFont(font)).setPadding(5));
                table.addCell(new Cell().add(new Paragraph(stock.getPartName() != null ? stock.getPartName() : "").setFont(font)).setPadding(5));
                table.addCell(new Cell().add(new Paragraph(stock.getStockQuantity() != null ? String.valueOf(stock.getStockQuantity()) : "0").setFont(font)).setTextAlignment(TextAlignment.RIGHT).setPadding(5));
                table.addCell(new Cell().add(new Paragraph(stock.getBinNumber() != null ? stock.getBinNumber() : "").setFont(font)).setPadding(5));
                table.addCell(new Cell().add(new Paragraph(stock.getRackNumber() != null ? stock.getRackNumber() : "").setFont(font)).setPadding(5));
                table.addCell(new Cell().add(new Paragraph(stock.getLastUpdated() != null ? stock.getLastUpdated().format(formatter) : "").setFont(font)).setPadding(5));
            }
            
            document.add(table);
            
            // Summary
            Paragraph summary = new Paragraph(String.format("Total Records: %d", stocks.size()))
                    .setFont(font)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(20);
            document.add(summary);
        }
        
        return baos.toByteArray();
    }
}

