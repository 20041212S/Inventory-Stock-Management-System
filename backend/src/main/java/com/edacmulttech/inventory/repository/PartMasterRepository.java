package com.edacmulttech.inventory.repository;

import com.edacmulttech.inventory.entity.PartMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartMasterRepository extends JpaRepository<PartMaster, String> {
    
    Optional<PartMaster> findByPartNumber(String partNumber);
    
    boolean existsByPartNumber(String partNumber);
    
    @Query("SELECT p FROM PartMaster p WHERE " +
           "LOWER(p.partNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.partName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<PartMaster> searchParts(@Param("searchTerm") String searchTerm);
    
    List<PartMaster> findByCategory(String category);
}

