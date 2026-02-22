package com.edacmulttech.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "location_name", unique = true, nullable = false)
    private String locationName;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<PartMaster> partMasters;
}

