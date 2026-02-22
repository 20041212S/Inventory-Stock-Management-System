package com.edacmulttech.inventory.service;

import com.edacmulttech.inventory.dto.BulkStockUploadDTO;
import com.edacmulttech.inventory.dto.PartMasterDTO;
import com.edacmulttech.inventory.dto.StockDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    public List<PartMasterDTO> readPartMasterFromExcel(MultipartFile file) throws IOException {
        List<PartMasterDTO> parts = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            
            // Skip header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (isRowEmpty(row)) {
                    continue;
                }
                
                PartMasterDTO dto = new PartMasterDTO();
                dto.setPartNumber(getCellValueAsString(row.getCell(0)));
                dto.setPartName(getCellValueAsString(row.getCell(1)));
                dto.setMakeOrBuy(getCellValueAsString(row.getCell(2)));
                dto.setCategory(getCellValueAsString(row.getCell(3)));
                dto.setSubcategory(getCellValueAsString(row.getCell(4)));
                dto.setUnitOfMeasure(getCellValueAsString(row.getCell(5)));
                dto.setDrawingNumber(getCellValueAsString(row.getCell(6)));
                dto.setRevisionNumber(getCellValueAsString(row.getCell(7)));
                
                // Handle date
                Cell dateCell = row.getCell(8);
                if (dateCell != null) {
                    if (dateCell.getCellType() == CellType.NUMERIC) {
                        Date date = dateCell.getDateCellValue();
                        dto.setRevisedDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    }
                }
                
                dto.setLocationName(getCellValueAsString(row.getCell(9)));
                
                parts.add(dto);
            }
        }
        
        return parts;
    }

    public List<BulkStockUploadDTO> readStockUploadFromExcel(MultipartFile file) throws IOException {
        List<BulkStockUploadDTO> stocks = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            
            // Skip header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (isRowEmpty(row)) {
                    continue;
                }
                
                BulkStockUploadDTO dto = new BulkStockUploadDTO();
                dto.setPartNumber(getCellValueAsString(row.getCell(0)));
                
                Cell stockCell = row.getCell(1);
                if (stockCell != null && stockCell.getCellType() == CellType.NUMERIC) {
                    dto.setStock(stockCell.getNumericCellValue());
                }
                
                dto.setBin(getCellValueAsString(row.getCell(2)));
                dto.setRack(getCellValueAsString(row.getCell(3)));
                
                stocks.add(dto);
            }
        }
        
        return stocks;
    }

    public byte[] exportStocksToExcel(List<StockDTO> stocks) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Stock Data");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Part Number", "Part Name", "Stock Quantity", "Bin Number", "Rack Number", "Last Updated"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }
            
            // Create data rows
            int rowNum = 1;
            for (StockDTO stock : stocks) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stock.getPartNumber());
                row.createCell(1).setCellValue(stock.getPartName() != null ? stock.getPartName() : "");
                row.createCell(2).setCellValue(stock.getStockQuantity());
                row.createCell(3).setCellValue(stock.getBinNumber() != null ? stock.getBinNumber() : "");
                row.createCell(4).setCellValue(stock.getRackNumber() != null ? stock.getRackNumber() : "");
                row.createCell(5).setCellValue(stock.getLastUpdated() != null ? stock.getLastUpdated().toString() : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] createStockUploadTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Stock Upload Template");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Part Number", "Stock", "Bin", "Rack"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}

