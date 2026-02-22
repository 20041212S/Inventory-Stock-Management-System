package com.edacmulttech.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartMasterDTO {
    private String partNumber;
    private String partName;
    private String makeOrBuy;
    private String category;
    private String subcategory;
    private String unitOfMeasure;
    private String drawingNumber;
    private String revisionNumber;
    private LocalDate revisedDate;
    private Long locationId;
    private String locationName;
}

