package com.edacmulttech.inventory.service;

import com.edacmulttech.inventory.dto.PartMasterDTO;
import com.edacmulttech.inventory.entity.Location;
import com.edacmulttech.inventory.entity.PartMaster;
import com.edacmulttech.inventory.repository.LocationRepository;
import com.edacmulttech.inventory.repository.PartMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartMasterService {

    private final PartMasterRepository partMasterRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Page<PartMasterDTO> getAllParts(Pageable pageable) {
        return partMasterRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<PartMasterDTO> searchParts(String searchTerm) {
        return partMasterRepository.searchParts(searchTerm)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PartMasterDTO getPartByPartNumber(String partNumber) {
        PartMaster part = partMasterRepository.findByPartNumber(partNumber)
                .orElseThrow(() -> new RuntimeException("Part not found with part number: " + partNumber));
        return convertToDTO(part);
    }

    @Transactional(readOnly = true)
    public List<PartMasterDTO> getAllPartsList() {
        return partMasterRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PartMasterDTO savePart(PartMasterDTO dto) {
        if (partMasterRepository.existsByPartNumber(dto.getPartNumber())) {
            throw new RuntimeException("Part number already exists: " + dto.getPartNumber());
        }

        PartMaster part = convertToEntity(dto);
        PartMaster saved = partMasterRepository.save(part);
        return convertToDTO(saved);
    }

    @Transactional
    public List<PartMasterDTO> saveAllParts(List<PartMasterDTO> dtos) {
        List<PartMaster> parts = dtos.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        
        List<PartMaster> saved = partMasterRepository.saveAll(parts);
        return saved.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PartMasterDTO convertToDTO(PartMaster part) {
        PartMasterDTO dto = new PartMasterDTO();
        dto.setPartNumber(part.getPartNumber());
        dto.setPartName(part.getPartName());
        dto.setMakeOrBuy(part.getMakeOrBuy());
        dto.setCategory(part.getCategory());
        dto.setSubcategory(part.getSubcategory());
        dto.setUnitOfMeasure(part.getUnitOfMeasure());
        dto.setDrawingNumber(part.getDrawingNumber());
        dto.setRevisionNumber(part.getRevisionNumber());
        dto.setRevisedDate(part.getRevisedDate());
        
        if (part.getLocation() != null) {
            dto.setLocationId(part.getLocation().getLocationId());
            dto.setLocationName(part.getLocation().getLocationName());
        }
        
        return dto;
    }

    private PartMaster convertToEntity(PartMasterDTO dto) {
        PartMaster part = new PartMaster();
        part.setPartNumber(dto.getPartNumber());
        part.setPartName(dto.getPartName());
        part.setMakeOrBuy(dto.getMakeOrBuy());
        part.setCategory(dto.getCategory());
        part.setSubcategory(dto.getSubcategory());
        part.setUnitOfMeasure(dto.getUnitOfMeasure());
        part.setDrawingNumber(dto.getDrawingNumber());
        part.setRevisionNumber(dto.getRevisionNumber());
        part.setRevisedDate(dto.getRevisedDate());
        
        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + dto.getLocationId()));
            part.setLocation(location);
        }
        
        return part;
    }
}

