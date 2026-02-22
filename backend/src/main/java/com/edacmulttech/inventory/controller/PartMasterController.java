package com.edacmulttech.inventory.controller;

import com.edacmulttech.inventory.dto.PartMasterDTO;
import com.edacmulttech.inventory.service.ExcelService;
import com.edacmulttech.inventory.service.PartMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parts")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PartMasterController {

    private final PartMasterService partMasterService;
    private final ExcelService excelService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        try {
            Map<String, Object> response = new HashMap<>();
            
            if (search != null && !search.trim().isEmpty()) {
                List<PartMasterDTO> parts = partMasterService.searchParts(search);
                response.put("parts", parts);
                response.put("totalElements", parts.size());
                response.put("totalPages", 1);
                response.put("currentPage", 0);
            } else {
                Pageable pageable = PageRequest.of(page, size);
                Page<PartMasterDTO> partsPage = partMasterService.getAllParts(pageable);
                response.put("parts", partsPage.getContent());
                response.put("totalElements", partsPage.getTotalElements());
                response.put("totalPages", partsPage.getTotalPages());
                response.put("currentPage", partsPage.getNumber());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PartMasterDTO>> getAllPartsList() {
        try {
            return ResponseEntity.ok(partMasterService.getAllPartsList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{partNumber}")
    public ResponseEntity<PartMasterDTO> getPartByPartNumber(@PathVariable String partNumber) {
        try {
            return ResponseEntity.ok(partMasterService.getPartByPartNumber(partNumber));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importPartsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<PartMasterDTO> parts = excelService.readPartMasterFromExcel(file);
            List<PartMasterDTO> saved = partMasterService.saveAllParts(parts);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Parts imported successfully");
            response.put("count", saved.size());
            response.put("parts", saved);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to import parts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}

