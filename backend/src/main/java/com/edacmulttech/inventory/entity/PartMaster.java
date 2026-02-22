package com.edacmulttech.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "part_master", indexes = {
    @Index(name = "idx_part_number", columnList = "partNumber"),
    @Index(name = "idx_category", columnList = "category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartMaster {

    @Id
    @Column(name = "part_number", unique = true, nullable = false)
    private String partNumber;

    @Column(name = "part_name", nullable = false)
    private String partName;

    @Column(name = "make_or_buy", nullable = false)
    private String makeOrBuy;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "subcategory")
    private String subcategory;

    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure;

    @Column(name = "drawing_number")
    private String drawingNumber;

    @Column(name = "revision_number")
    private String revisionNumber;

    @Column(name = "revised_date")
    private LocalDate revisedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
}

