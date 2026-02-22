package com.edacmulttech.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock", indexes = {
    @Index(name = "idx_part_number_stock", columnList = "partNumber"),
    @Index(name = "idx_bin_number", columnList = "binNumber"),
    @Index(name = "idx_rack_number", columnList = "rackNumber")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_number", nullable = false)
    private PartMaster partMaster;

    @Column(name = "part_number", insertable = false, updatable = false)
    private String partNumber;

    @Column(name = "stock_quantity", nullable = false)
    private Double stockQuantity;

    @Column(name = "bin_number")
    private String binNumber;

    @Column(name = "rack_number")
    private String rackNumber;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}

