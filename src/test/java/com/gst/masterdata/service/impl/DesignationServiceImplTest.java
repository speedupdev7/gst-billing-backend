package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.DesignationRequest;
import com.gst.masterdata.entity.DesignationMasterEntity;
import jakarta.persistence.EntityNotFoundException;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.DesignationMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesignationServiceImplTest {

    @Mock
    private DesignationMasterRepository repository;

    @InjectMocks
    private DesignationServiceImpl service;

    private DesignationMasterEntity entity;

    @BeforeEach
    void setUp() {
        entity = new DesignationMasterEntity();
        entity.setDesignationId(1L);
        entity.setDesignationCode("DES01");
        entity.setDesignationName("Manager");
        entity.setIsActive(true);
    }

    @Test
    void getAll_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll((org.springframework.data.jpa.domain.Specification<DesignationMasterEntity>) any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        var result = service.getAll("Man", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Manager", result.getContent().get(0).getDesignationName());
    }

    @Test
    void getById_shouldReturnResponse_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        var result = service.getById(1L);

        assertEquals("Manager", result.getDesignationName());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void create_shouldPersistAndReturnResponse() {
        DesignationRequest request = new DesignationRequest();
        request.setDesignationCode("DES02");
        request.setDesignationName("Developer");
        when(repository.save(any(DesignationMasterEntity.class))).thenAnswer(invocation -> {
            DesignationMasterEntity saved = invocation.getArgument(0);
            saved.setDesignationId(2L);
            return saved;
        });

        var result = service.create(request);

        assertEquals(2L, result.getDesignationId());
        assertEquals("Developer", result.getDesignationName());
    }

    @Test
    void delete_shouldMarkDeleted() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        assertTrue(entity.getIsDeleted());
        assertNotNull(entity.getDeletedAt());
    }
}
