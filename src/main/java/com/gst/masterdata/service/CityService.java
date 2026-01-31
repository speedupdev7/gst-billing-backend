package com.gst.masterdata.service;

import com.gst.masterdata.dto.CityRequest;
import com.gst.masterdata.dto.CityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityService {
    Page<CityResponse> getCities(String cityCode, String cityName, Boolean isActive, Pageable pageable);
    CityResponse getById(Long id);
    CityResponse create(CityRequest request);
    CityResponse update(Long id, CityRequest request);
    void delete(Long id);
}

