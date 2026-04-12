package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.SupplierMasterDTO;
import com.gst.masterdata.entity.SupplierMasterEntity;
import com.gst.masterdata.repository.SupplierMasterRepository;
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
class SupplierMasterServiceImplTest {

    @Mock
    private SupplierMasterRepository supplierMasterRepository;

    @InjectMocks
    private SupplierMasterServiceImpl service;

    private SupplierMasterDTO dto;
    private SupplierMasterEntity entity;

    @BeforeEach
    void setUp() {
        dto = new SupplierMasterDTO();
        dto.setSupplierName("Supplier A");
        dto.setEmail("supplier@acme.com");

        entity = new SupplierMasterEntity();
        entity.setSupplierId(1L);
        entity.setSupplierName(dto.getSupplierName());
        entity.setEmail(dto.getEmail());
    }

    @Test
    void createSupplier_shouldReturnSavedDto() {
        when(supplierMasterRepository.save(any(SupplierMasterEntity.class))).thenReturn(entity);

        SupplierMasterDTO result = service.createSupplier(dto);

        assertNotNull(result);
        assertEquals(1L, result.getSupplierId());
        assertEquals("Supplier A", result.getSupplierName());
    }

    @Test
    void getSupplierById_shouldReturnDto_whenFound() {
        when(supplierMasterRepository.findById(1L)).thenReturn(Optional.of(entity));

        SupplierMasterDTO result = service.getSupplierById(1L);

        assertEquals("Supplier A", result.getSupplierName());
    }

    @Test
    void getSupplierById_shouldThrow_whenNotFound() {
        when(supplierMasterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getSupplierById(1L));
    }

    @Test
    void getAllSuppliers_shouldReturnList() {
        when(supplierMasterRepository.findAll(any(Sort.class))).thenReturn(List.of(entity));

        List<SupplierMasterDTO> result = service.getAllSuppliers();

        assertEquals(1, result.size());
        assertEquals("Supplier A", result.get(0).getSupplierName());
    }

    @Test
    void deleteSupplier_shouldSoftDeleteEntity() {
        when(supplierMasterRepository.findById(1L)).thenReturn(Optional.of(entity));

        service.deleteSupplier(1L);

        assertTrue(entity.getIsDeleted());
        assertNotNull(entity.getDeletedAt());
        verify(supplierMasterRepository, never()).deleteById(any());
    }
}
