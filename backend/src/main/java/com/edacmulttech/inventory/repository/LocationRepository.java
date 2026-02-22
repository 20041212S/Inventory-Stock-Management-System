package com.edacmulttech.inventory.repository;

import com.edacmulttech.inventory.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Optional<Location> findByLocationName(String locationName);
    
    boolean existsByLocationName(String locationName);
}

