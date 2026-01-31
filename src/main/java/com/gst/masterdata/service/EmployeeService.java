package com.gst.masterdata.service;

import com.gst.masterdata.dto.EmployeeRequest;
import com.gst.masterdata.dto.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Page<EmployeeResponse> getEmployees(String employeeCode, String employeeName, Boolean isActive, Pageable pageable);
    EmployeeResponse getById(Long id);
    EmployeeResponse create(EmployeeRequest request);
    EmployeeResponse update(Long id, EmployeeRequest request);
    void delete(Long id);
}

