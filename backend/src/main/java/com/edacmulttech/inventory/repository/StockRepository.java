package com.edacmulttech.inventory.repository;

import com.edacmulttech.inventory.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    List<Stock> findByPartNumber(String partNumber);
    
    Optional<Stock> findByPartNumberAndBinNumberAndRackNumber(
        String partNumber, String binNumber, String rackNumber);
    
    @Query("SELECT s FROM Stock s WHERE s.partNumber = :partNumber")
    List<Stock> findAllByPartNumber(@Param("partNumber") String partNumber);
    
    @Query("SELECT DISTINCT s.binNumber FROM Stock s WHERE s.partNumber = :partNumber")
    List<String> findDistinctBinNumbersByPartNumber(@Param("partNumber") String partNumber);
}

