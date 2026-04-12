package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.RoleRequest;
import com.gst.masterdata.entity.RoleMasterEntity;
import com.gst.masterdata.exceptions.BusinessException;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.RoleMasterRepository;
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
class RoleServiceImplTest {

    @Mock
    private RoleMasterRepository repository;

    @InjectMocks
    private RoleServiceImpl service;

    private RoleMasterEntity entity;

    @BeforeEach
    void setUp() {
        entity = new RoleMasterEntity();
        entity.setRoleId(1L);
        entity.setRoleCode("ADMIN");
        entity.setRoleName("Administrator");
        entity.setDescription("Admin role");
        entity.setIsActive(true);
        entity.setIsSystemRole(false);
    }

    @Test
    void getRoles_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll((org.springframework.data.jpa.domain.Specification<RoleMasterEntity>) any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        var result = service.getRoles("AD", null, true, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("ADMIN", result.getContent().get(0).getRoleCode());
    }

    @Test
    void getById_shouldReturnRoleResponse_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        var result = service.getById(1L);

        assertEquals("ADMIN", result.getRoleCode());
        assertEquals("NORMAL", result.getRoleType());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void create_shouldPersistAndReturnResponse() {
        RoleRequest request = new RoleRequest();
        request.setRoleCode("TEST");
        request.setRoleName("Test Role");
        request.setDescription("Test description");
        when(repository.save(any(RoleMasterEntity.class))).thenAnswer(invocation -> {
            RoleMasterEntity saved = invocation.getArgument(0);
            saved.setRoleId(2L);
            return saved;
        });

        var result = service.create(request);

        assertEquals(2L, result.getRoleId());
        assertEquals("TEST", result.getRoleCode());
        assertTrue(result.getIsActive());
    }

    @Test
    void delete_shouldThrowWhenSystemRole() {
        entity.setIsSystemRole(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(BusinessException.class, () -> service.delete(1L));
    }
}
