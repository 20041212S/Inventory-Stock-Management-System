package com.edacmulttech.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkStockUploadDTO {
    private String partNumber;
    private Double stock;
    private String bin;
    private String rack;
}

