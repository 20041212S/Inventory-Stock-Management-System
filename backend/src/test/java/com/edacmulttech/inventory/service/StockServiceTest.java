package com.edacmulttech.inventory.service;

import com.edacmulttech.inventory.dto.BulkStockUploadDTO;
import com.edacmulttech.inventory.dto.BulkUploadResponse;
import com.edacmulttech.inventory.dto.StockDTO;
import com.edacmulttech.inventory.entity.PartMaster;
import com.edacmulttech.inventory.entity.Stock;
import com.edacmulttech.inventory.repository.PartMasterRepository;
import com.edacmulttech.inventory.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private PartMasterRepository partMasterRepository;

    @InjectMocks
    private StockService stockService;

    private Stock testStock;
    private PartMaster testPart;
    private StockDTO testStockDTO;

    @BeforeEach
    void setUp() {
        testPart = new PartMaster();
        testPart.setPartNumber("PART001");
        testPart.setPartName("Test Part");

        testStock = new Stock();
        testStock.setStockId(1L);
        testStock.setPartMaster(testPart);
        testStock.setPartNumber("PART001");
        testStock.setStockQuantity(100.0);
        testStock.setBinNumber("BIN001");
        testStock.setRackNumber("RACK001");
        testStock.setLastUpdated(LocalDateTime.now());

        testStockDTO = new StockDTO();
        testStockDTO.setPartNumber("PART001");
        testStockDTO.setStockQuantity(100.0);
        testStockDTO.setBinNumber("BIN001");
        testStockDTO.setRackNumber("RACK001");
    }

    @Test
    void testGetAllStocks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stockPage = new PageImpl<>(Arrays.asList(testStock));
        
        when(stockRepository.findAll(pageable)).thenReturn(stockPage);

        Page<StockDTO> result = stockService.getAllStocks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("PART001", result.getContent().get(0).getPartNumber());
        verify(stockRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetStockById_Success() {
        when(stockRepository.findById(1L)).thenReturn(Optional.of(testStock));

        StockDTO result = stockService.getStockById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getStockId());
        assertEquals("PART001", result.getPartNumber());
        verify(stockRepository, times(1)).findById(1L);
    }

    @Test
    void testGetStockById_NotFound() {
        when(stockRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            stockService.getStockById(999L);
        });
    }

    @Test
    void testCreateStock_Success() {
        when(partMasterRepository.findByPartNumber("PART001")).thenReturn(Optional.of(testPart));
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);

        StockDTO result = stockService.createStock(testStockDTO);

        assertNotNull(result);
        assertEquals("PART001", result.getPartNumber());
        verify(partMasterRepository, times(1)).findByPartNumber("PART001");
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testCreateStock_PartNotFound() {
        when(partMasterRepository.findByPartNumber("INVALID")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            stockService.createStock(testStockDTO);
        });
    }

    @Test
    void testUpdateStock_Success() {
        when(stockRepository.findById(1L)).thenReturn(Optional.of(testStock));
        when(partMasterRepository.findByPartNumber("PART001")).thenReturn(Optional.of(testPart));
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);

        StockDTO result = stockService.updateStock(1L, testStockDTO);

        assertNotNull(result);
        verify(stockRepository, times(1)).findById(1L);
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testDeleteStock_Success() {
        when(stockRepository.existsById(1L)).thenReturn(true);
        doNothing().when(stockRepository).deleteById(1L);

        stockService.deleteStock(1L);

        verify(stockRepository, times(1)).existsById(1L);
        verify(stockRepository, times(1)).deleteById(1L);
    }

    @Test
    void testBulkUploadStocks_Success() {
        BulkStockUploadDTO uploadDTO = new BulkStockUploadDTO();
        uploadDTO.setPartNumber("PART001");
        uploadDTO.setStock(50.0);
        uploadDTO.setBin("BIN002");
        uploadDTO.setRack("RACK002");

        when(partMasterRepository.existsByPartNumber("PART001")).thenReturn(true);
        when(partMasterRepository.findByPartNumber("PART001")).thenReturn(Optional.of(testPart));
        when(stockRepository.findByPartNumber("PART001")).thenReturn(Collections.emptyList());
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);

        BulkUploadResponse response = stockService.bulkUploadStocks(Arrays.asList(uploadDTO));

        assertNotNull(response);
        assertEquals(1, response.getTotalRecords());
        assertEquals(1, response.getSuccessCount());
        assertEquals(0, response.getFailureCount());
    }

    @Test
    void testBulkUploadStocks_PartNotFound() {
        BulkStockUploadDTO uploadDTO = new BulkStockUploadDTO();
        uploadDTO.setPartNumber("INVALID");
        uploadDTO.setStock(50.0);

        when(partMasterRepository.existsByPartNumber("INVALID")).thenReturn(false);

        BulkUploadResponse response = stockService.bulkUploadStocks(Arrays.asList(uploadDTO));

        assertNotNull(response);
        assertEquals(1, response.getTotalRecords());
        assertEquals(0, response.getSuccessCount());
        assertEquals(1, response.getFailureCount());
        assertTrue(response.getErrors().size() > 0);
    }
}

