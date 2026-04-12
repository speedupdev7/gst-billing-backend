package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.ExpensesRequest;
import com.gst.masterdata.entity.ExpenseMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.ExpenseMasterRepository;
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
class ExpensesServiceImplTest {

    @Mock
    private ExpenseMasterRepository repository;

    @InjectMocks
    private ExpensesServiceImpl service;

    private ExpenseMasterEntity entity;

    @BeforeEach
    void setUp() {
        entity = new ExpenseMasterEntity();
        entity.setExpenseId(1L);
        entity.setExpenseCode("EXP01");
        entity.setExpenseName("Travel");
        entity.setDescription("Travel expense");
        entity.setIsReimbursable(true);
    }

    @Test
    void getExpenses_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll((org.springframework.data.jpa.domain.Specification<ExpenseMasterEntity>) any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        var result = service.getExpenses("EXP01", null, true, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Travel", result.getContent().get(0).getExpenseName());
    }

    @Test
    void getById_shouldReturnResponse_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        var result = service.getById(1L);

        assertEquals("Travel", result.getExpenseName());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void create_shouldReturnSavedResponse() {
        ExpensesRequest request = new ExpensesRequest();
        request.setExpenseCode("EXP02");
        request.setExpenseName("Stationery");
        request.setDescription("Stationery expense");
        request.setIsReimbursable(false);
        when(repository.save(any(ExpenseMasterEntity.class))).thenAnswer(invocation -> {
            ExpenseMasterEntity saved = invocation.getArgument(0);
            saved.setExpenseId(2L);
            return saved;
        });

        var result = service.create(request);

        assertEquals(2L, result.getExpenseId());
        assertEquals("Stationery", result.getExpenseName());
        assertFalse(result.getIsReimbursable());
    }

    @Test
    void delete_shouldMarkDeleted() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        assertTrue(entity.getIsDeleted());
        assertNotNull(entity.getDeletedAt());
    }
}
