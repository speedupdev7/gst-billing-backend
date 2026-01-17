package com.example.masterdata.service;

import com.example.masterdata.dto.CityRequest;
import com.example.masterdata.dto.CityResponse;
import com.example.masterdata.dto.ExpensesRequest;
import com.example.masterdata.dto.ExpensesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpensesService {
    Page<ExpensesResponse> getExpenses(String expensesCode, String expensesName, Boolean isActive, Pageable pageable);
    ExpensesResponse getById(Long id);
    ExpensesResponse create(ExpensesRequest request);
    ExpensesResponse update(Long id, ExpensesRequest request);
    void delete(Long id);
}

