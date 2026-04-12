package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.UnitMasterDTO;
import com.gst.masterdata.entity.UnitMasterEntity;
import com.gst.masterdata.repository.UnitMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitMasterServiceImplTest {

    @Mock
    private UnitMasterRepository unitMasterRepository;

    @InjectMocks
    private UnitMasterServiceImpl service;

    private UnitMasterDTO dto;
    private UnitMasterEntity entity;

    @BeforeEach
    void setUp() {
        dto = new UnitMasterDTO();
        dto.setUnitName("Kilogram");

        entity = new UnitMasterEntity();
        entity.setUnitId(1L);
        entity.setUnitName(dto.getUnitName());
    }

    @Test
    void createUnit_shouldReturnSavedDto() {
        when(unitMasterRepository.save(any(UnitMasterEntity.class))).thenReturn(entity);

        UnitMasterDTO result = service.createUnit(dto);

        assertNotNull(result);
        assertEquals(1L, result.getUnitId());
        assertEquals("Kilogram", result.getUnitName());
    }

    @Test
    void getUnitById_shouldReturnDto_whenFound() {
        when(unitMasterRepository.findById(1L)).thenReturn(Optional.of(entity));

        UnitMasterDTO result = service.getUnitById(1L);

        assertEquals("Kilogram", result.getUnitName());
    }

    @Test
    void getUnitById_shouldThrow_whenNotFound() {
        when(unitMasterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getUnitById(1L));
    }

    @Test
    void getAllUnits_shouldReturnList() {
        when(unitMasterRepository.findAll(any(Sort.class))).thenReturn(List.of(entity));

        List<UnitMasterDTO> result = service.getAllUnits();

        assertEquals(1, result.size());
        assertEquals("Kilogram", result.get(0).getUnitName());
    }

    @Test
    void deleteUnit_shouldSoftDeleteEntity() {
        when(unitMasterRepository.findById(1L)).thenReturn(Optional.of(entity));

        service.deleteUnit(1L);

        assertTrue(entity.getIsDeleted());
        assertNotNull(entity.getDeletedAt());
        verify(unitMasterRepository, never()).deleteById(any());
    }
}
