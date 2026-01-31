package com.gst.masterdata.service;

import com.gst.masterdata.dto.ExpensesRequest;
import com.gst.masterdata.dto.ExpensesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpensesService {
    Page<ExpensesResponse> getExpenses(String expensesCode, String expensesName, Boolean isActive, Pageable pageable);
    ExpensesResponse getById(Long id);
    ExpensesResponse create(ExpensesRequest request);
    ExpensesResponse update(Long id, ExpensesRequest request);
    void delete(Long id);
}

