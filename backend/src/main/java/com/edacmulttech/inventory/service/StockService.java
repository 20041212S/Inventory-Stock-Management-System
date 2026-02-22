package com.edacmulttech.inventory.service;

import com.edacmulttech.inventory.dto.BulkStockUploadDTO;
import com.edacmulttech.inventory.dto.BulkUploadResponse;
import com.edacmulttech.inventory.dto.StockDTO;
import com.edacmulttech.inventory.entity.PartMaster;
import com.edacmulttech.inventory.entity.Stock;
import com.edacmulttech.inventory.repository.PartMasterRepository;
import com.edacmulttech.inventory.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final PartMasterRepository partMasterRepository;

    @Transactional(readOnly = true)
    public Page<StockDTO> getAllStocks(Pageable pageable) {
        return stockRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<StockDTO> getAllStocksList() {
        return stockRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StockDTO getStockById(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found with id: " + stockId));
        return convertToDTO(stock);
    }

    @Transactional(readOnly = true)
    public List<StockDTO> getStocksByPartNumber(String partNumber) {
        return stockRepository.findByPartNumber(partNumber)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StockDTO createStock(StockDTO dto) {
        PartMaster part = partMasterRepository.findByPartNumber(dto.getPartNumber())
                .orElseThrow(() -> new RuntimeException("Part not found with part number: " + dto.getPartNumber()));

        Stock stock = new Stock();
        stock.setPartMaster(part);
        stock.setPartNumber(dto.getPartNumber());
        stock.setStockQuantity(dto.getStockQuantity());
        stock.setBinNumber(dto.getBinNumber());
        stock.setRackNumber(dto.getRackNumber());
        stock.setLastUpdated(java.time.LocalDateTime.now());

        Stock saved = stockRepository.save(stock);
        return convertToDTO(saved);
    }

    @Transactional
    public StockDTO updateStock(Long stockId, StockDTO dto) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found with id: " + stockId));

        if (!stock.getPartNumber().equals(dto.getPartNumber())) {
            PartMaster part = partMasterRepository.findByPartNumber(dto.getPartNumber())
                    .orElseThrow(() -> new RuntimeException("Part not found with part number: " + dto.getPartNumber()));
            stock.setPartMaster(part);
            stock.setPartNumber(dto.getPartNumber());
        }

        stock.setStockQuantity(dto.getStockQuantity());
        stock.setBinNumber(dto.getBinNumber());
        stock.setRackNumber(dto.getRackNumber());

        Stock updated = stockRepository.save(stock);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteStock(Long stockId) {
        if (!stockRepository.existsById(stockId)) {
            throw new RuntimeException("Stock not found with id: " + stockId);
        }
        stockRepository.deleteById(stockId);
    }

    @Transactional
    public BulkUploadResponse bulkUploadStocks(List<BulkStockUploadDTO> uploadDTOs) {
        BulkUploadResponse response = new BulkUploadResponse();
        response.setTotalRecords(uploadDTOs.size());
        response.setErrors(new ArrayList<>());
        response.setWarnings(new ArrayList<>());
        
        Map<String, List<BulkStockUploadDTO>> partGroups = uploadDTOs.stream()
                .collect(Collectors.groupingBy(BulkStockUploadDTO::getPartNumber));

        int successCount = 0;
        int failureCount = 0;

        for (Map.Entry<String, List<BulkStockUploadDTO>> entry : partGroups.entrySet()) {
            String partNumber = entry.getKey();
            List<BulkStockUploadDTO> records = entry.getValue();

            // Check if part exists
            if (!partMasterRepository.existsByPartNumber(partNumber)) {
                response.getErrors().add("Part number '" + partNumber + "' does not exist in master data. All records rejected.");
                failureCount += records.size();
                continue;
            }

            // Check for different bin numbers
            Set<String> distinctBins = records.stream()
                    .map(BulkStockUploadDTO::getBin)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            if (distinctBins.size() > 1) {
                response.getWarnings().add("Part number '" + partNumber + "' has different bin numbers: " + distinctBins);
            }

            // Aggregate stock quantities
            double totalStock = records.stream()
                    .mapToDouble(dto -> dto.getStock() != null ? dto.getStock() : 0.0)
                    .sum();

            // Get or create stock entry
            PartMaster part = partMasterRepository.findByPartNumber(partNumber)
                    .orElseThrow(() -> new RuntimeException("Part not found: " + partNumber));

            List<Stock> existingStocks = stockRepository.findByPartNumber(partNumber);
            
            if (existingStocks.isEmpty()) {
                // Create new stock entry
                Stock stock = new Stock();
                stock.setPartMaster(part);
                stock.setPartNumber(partNumber);
                stock.setStockQuantity(totalStock);
                stock.setBinNumber(records.get(0).getBin());
                stock.setRackNumber(records.get(0).getRack());
                stock.setLastUpdated(java.time.LocalDateTime.now());
                stockRepository.save(stock);
                successCount++;
            } else {
                // Update existing stock - aggregate quantities
                Stock stock = existingStocks.get(0);
                stock.setStockQuantity(stock.getStockQuantity() + totalStock);
                if (records.get(0).getBin() != null && !records.get(0).getBin().isEmpty()) {
                    stock.setBinNumber(records.get(0).getBin());
                }
                if (records.get(0).getRack() != null && !records.get(0).getRack().isEmpty()) {
                    stock.setRackNumber(records.get(0).getRack());
                }
                stockRepository.save(stock);
                successCount++;
            }
        }

        response.setSuccessCount(successCount);
        response.setFailureCount(failureCount);
        return response;
    }

    private StockDTO convertToDTO(Stock stock) {
        StockDTO dto = new StockDTO();
        dto.setStockId(stock.getStockId());
        dto.setPartNumber(stock.getPartNumber());
        dto.setStockQuantity(stock.getStockQuantity());
        dto.setBinNumber(stock.getBinNumber());
        dto.setRackNumber(stock.getRackNumber());
        dto.setLastUpdated(stock.getLastUpdated());
        
        if (stock.getPartMaster() != null) {
            dto.setPartName(stock.getPartMaster().getPartName());
        }
        
        return dto;
    }
}

