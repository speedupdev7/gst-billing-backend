package com.gst.masterdata.service;

import com.gst.masterdata.dto.DesignationRequest;
import com.gst.masterdata.dto.DesignationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DesignationService {
    Page<DesignationResponse> getAll(String designationName, Pageable pageable);
    DesignationResponse getById(Long id);
    DesignationResponse create(DesignationRequest request);
    DesignationResponse update(Long id, DesignationRequest request);
    void delete(Long id);
}

