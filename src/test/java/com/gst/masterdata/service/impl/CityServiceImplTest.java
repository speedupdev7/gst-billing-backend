package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.CityRequest;
import com.gst.masterdata.entity.CityMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.CityMasterRepository;
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
class CityServiceImplTest {

    @Mock
    private CityMasterRepository repository;

    @InjectMocks
    private CityServiceImpl service;

    private CityMasterEntity entity;

    @BeforeEach
    void setUp() {
        entity = new CityMasterEntity();
        entity.setCityId(1L);
        entity.setCityCode("MUM");
        entity.setCityName("Mumbai");
        entity.setStateName("Maharashtra");
        entity.setCountry("India");
        entity.setIsActive(true);
    }

    @Test
    void getCities_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll((org.springframework.data.jpa.domain.Specification<CityMasterEntity>) any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        var result = service.getCities("MUM", null, true, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Mumbai", result.getContent().get(0).getCityName());
    }

    @Test
    void getById_shouldReturnWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        var result = service.getById(1L);

        assertEquals("Mumbai", result.getCityName());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void create_shouldPersistAndReturnResponse() {
        CityRequest request = new CityRequest();
        request.setCityCode("PUN");
        request.setCityName("Pune");
        request.setStateName("Maharashtra");
        request.setCountry("India");
        when(repository.save(any(CityMasterEntity.class))).thenAnswer(invocation -> {
            CityMasterEntity saved = invocation.getArgument(0);
            saved.setCityId(2L);
            return saved;
        });

        var result = service.create(request);

        assertEquals(2L, result.getCityId());
        assertEquals("Pune", result.getCityName());
    }

    @Test
    void delete_shouldMarkEntityDeleted() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        assertTrue(entity.getIsDeleted());
        assertNotNull(entity.getDeletedAt());
    }
}
