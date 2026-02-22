package com.edacmulttech.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResponse {
    private int totalRecords;
    private int successCount;
    private int failureCount;
    private List<String> errors;
    private List<String> warnings;
}

