package com.edacmulttech.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private Long stockId;
    private String partNumber;
    private String partName;
    private Double stockQuantity;
    private String binNumber;
    private String rackNumber;
    private LocalDateTime lastUpdated;
}

