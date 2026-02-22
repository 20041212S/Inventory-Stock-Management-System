package com.edacmulttech.inventory.controller;

import com.edacmulttech.inventory.dto.BulkStockUploadDTO;
import com.edacmulttech.inventory.dto.BulkUploadResponse;
import com.edacmulttech.inventory.dto.StockDTO;
import com.edacmulttech.inventory.service.ExcelService;
import com.edacmulttech.inventory.service.PdfService;
import com.edacmulttech.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final ExcelService excelService;
    private final PdfService pdfService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<StockDTO> stocksPage = stockService.getAllStocks(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("stocks", stocksPage.getContent());
            response.put("totalElements", stocksPage.getTotalElements());
            response.put("totalPages", stocksPage.getTotalPages());
            response.put("currentPage", stocksPage.getNumber());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<StockDTO>> getAllStocksList() {
        try {
            return ResponseEntity.ok(stockService.getAllStocksList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<StockDTO> getStockById(@PathVariable Long stockId) {
        try {
            return ResponseEntity.ok(stockService.getStockById(stockId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/part/{partNumber}")
    public ResponseEntity<List<StockDTO>> getStocksByPartNumber(@PathVariable String partNumber) {
        try {
            return ResponseEntity.ok(stockService.getStocksByPartNumber(partNumber));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<StockDTO> createStock(@RequestBody StockDTO stockDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(stockService.createStock(stockDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{stockId}")
    public ResponseEntity<StockDTO> updateStock(@PathVariable Long stockId, @RequestBody StockDTO stockDTO) {
        try {
            return ResponseEntity.ok(stockService.updateStock(stockId, stockDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{stockId}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long stockId) {
        try {
            stockService.deleteStock(stockId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<BulkUploadResponse> bulkUploadStocks(@RequestParam("file") MultipartFile file) {
        try {
            List<BulkStockUploadDTO> uploadDTOs = excelService.readStockUploadFromExcel(file);
            BulkUploadResponse response = stockService.bulkUploadStocks(uploadDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BulkUploadResponse errorResponse = new BulkUploadResponse();
            errorResponse.setErrors(List.of("Failed to process file: " + e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/template/download")
    public ResponseEntity<byte[]> downloadStockUploadTemplate() {
        try {
            byte[] template = excelService.createStockUploadTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "stock_upload_template.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(template);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportStocksToExcel() {
        try {
            List<StockDTO> stocks = stockService.getAllStocksList();
            byte[] excelData = excelService.exportStocksToExcel(stocks);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "stock_inventory.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportStocksToPdf() {
        try {
            List<StockDTO> stocks = stockService.getAllStocksList();
            byte[] pdfData = pdfService.exportStocksToPdf(stocks);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "stock_inventory.pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

