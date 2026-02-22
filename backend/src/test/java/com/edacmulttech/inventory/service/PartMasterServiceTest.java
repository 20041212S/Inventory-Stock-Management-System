package com.edacmulttech.inventory.service;

import com.edacmulttech.inventory.dto.PartMasterDTO;
import com.edacmulttech.inventory.entity.Location;
import com.edacmulttech.inventory.entity.PartMaster;
import com.edacmulttech.inventory.repository.LocationRepository;
import com.edacmulttech.inventory.repository.PartMasterRepository;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartMasterServiceTest {

    @Mock
    private PartMasterRepository partMasterRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private PartMasterService partMasterService;

    private PartMaster testPart;
    private Location testLocation;
    private PartMasterDTO testPartDTO;

    @BeforeEach
    void setUp() {
        testLocation = new Location();
        testLocation.setLocationId(1L);
        testLocation.setLocationName("Warehouse A");

        testPart = new PartMaster();
        testPart.setPartNumber("PART001");
        testPart.setPartName("Test Part");
        testPart.setMakeOrBuy("Make");
        testPart.setCategory("Raw Material");
        testPart.setSubcategory("Aluminum");
        testPart.setUnitOfMeasure("kg");
        testPart.setLocation(testLocation);

        testPartDTO = new PartMasterDTO();
        testPartDTO.setPartNumber("PART001");
        testPartDTO.setPartName("Test Part");
        testPartDTO.setMakeOrBuy("Make");
        testPartDTO.setCategory("Raw Material");
        testPartDTO.setSubcategory("Aluminum");
        testPartDTO.setUnitOfMeasure("kg");
        testPartDTO.setLocationId(1L);
    }

    @Test
    void testGetAllParts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PartMaster> partPage = new PageImpl<>(Arrays.asList(testPart));
        
        when(partMasterRepository.findAll(pageable)).thenReturn(partPage);

        Page<PartMasterDTO> result = partMasterService.getAllParts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("PART001", result.getContent().get(0).getPartNumber());
        verify(partMasterRepository, times(1)).findAll(pageable);
    }

    @Test
    void testSearchParts() {
        String searchTerm = "PART001";
        when(partMasterRepository.searchParts(searchTerm)).thenReturn(Arrays.asList(testPart));

        List<PartMasterDTO> result = partMasterService.searchParts(searchTerm);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PART001", result.get(0).getPartNumber());
        verify(partMasterRepository, times(1)).searchParts(searchTerm);
    }

    @Test
    void testGetPartByPartNumber_Success() {
        when(partMasterRepository.findByPartNumber("PART001")).thenReturn(Optional.of(testPart));

        PartMasterDTO result = partMasterService.getPartByPartNumber("PART001");

        assertNotNull(result);
        assertEquals("PART001", result.getPartNumber());
        assertEquals("Test Part", result.getPartName());
        verify(partMasterRepository, times(1)).findByPartNumber("PART001");
    }

    @Test
    void testGetPartByPartNumber_NotFound() {
        when(partMasterRepository.findByPartNumber("INVALID")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            partMasterService.getPartByPartNumber("INVALID");
        });
    }

    @Test
    void testSavePart_Success() {
        when(partMasterRepository.existsByPartNumber("PART001")).thenReturn(false);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(partMasterRepository.save(any(PartMaster.class))).thenReturn(testPart);

        PartMasterDTO result = partMasterService.savePart(testPartDTO);

        assertNotNull(result);
        assertEquals("PART001", result.getPartNumber());
        verify(partMasterRepository, times(1)).existsByPartNumber("PART001");
        verify(partMasterRepository, times(1)).save(any(PartMaster.class));
    }

    @Test
    void testSavePart_DuplicatePartNumber() {
        when(partMasterRepository.existsByPartNumber("PART001")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            partMasterService.savePart(testPartDTO);
        });
    }
}

