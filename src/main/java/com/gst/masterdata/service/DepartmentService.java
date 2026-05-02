package com.gst.masterdata.service;

import com.gst.masterdata.dto.DepartmentRequest;
import com.gst.masterdata.dto.DepartmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    Page<DepartmentResponse> getDepartments(String departmentCode, String departmentName, Boolean isActive, Pageable pageable);
    DepartmentResponse getById(Long id);
    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse update(Long id, DepartmentRequest request);
    void delete(Long id);
}