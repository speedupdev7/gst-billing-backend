package com.gst.masterdata.service;

import com.gst.masterdata.dto.QualificationRequest;
import com.gst.masterdata.dto.QualificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QualificationService {
    Page<QualificationResponse> getAll(String qualificationName, Pageable pageable);
    QualificationResponse getById(Long id);
    QualificationResponse create(QualificationRequest request);
    QualificationResponse update(Long id, QualificationRequest request);
    void delete(Long id);
}

