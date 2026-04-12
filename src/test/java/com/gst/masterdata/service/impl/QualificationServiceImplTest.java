package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.QualificationRequest;
import com.gst.masterdata.entity.QualificationMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.QualificationMasterRepository;
import jakarta.persistence.EntityNotFoundException;
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
class QualificationServiceImplTest {

    @Mock
    private QualificationMasterRepository repository;

    @InjectMocks
    private QualificationServiceImpl service;

    private QualificationMasterEntity entity;

    @BeforeEach
    void setUp() {
        entity = new QualificationMasterEntity();
        entity.setQualificationId(1L);
        entity.setQualificationCode("QUAL01");
        entity.setQualificationName("Graduate");
        entity.setDescription("Graduation Degree");
        entity.setIsActive(true);
    }

    @Test
    void getAll_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll((org.springframework.data.jpa.domain.Specification<QualificationMasterEntity>) any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        var result = service.getAll("Grad", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Graduate", result.getContent().get(0).getQualificationName());
    }

    @Test
    void getById_shouldReturnResponse_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        var result = service.getById(1L);

        assertEquals("Graduate", result.getQualificationName());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void create_shouldReturnMappedResponse() {
        QualificationRequest request = new QualificationRequest();
        request.setQualificationCode("QUAL02");
        request.setQualificationName("Postgraduate");
        request.setDescription("PG Degree");
        when(repository.save(any(QualificationMasterEntity.class))).thenAnswer(invocation -> {
            QualificationMasterEntity saved = invocation.getArgument(0);
            saved.setQualificationId(2L);
            return saved;
        });

        var result = service.create(request);

        assertEquals(2L, result.getQualificationId());
        assertEquals("Postgraduate", result.getQualificationName());
    }

    @Test
    void delete_shouldMarkEntityDeleted() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        assertTrue(entity.getIsDeleted());
        assertNotNull(entity.getDeletedAt());
    }
}
