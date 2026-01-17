package com.example.masterdata.service;

import com.example.masterdata.dto.CityRequest;
import com.example.masterdata.dto.CityResponse;
import com.example.masterdata.dto.RoleRequest;
import com.example.masterdata.dto.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityService {
    Page<CityResponse> getCities(String cityCode, String cityName, Boolean isActive, Pageable pageable);
    CityResponse getById(Long id);
    CityResponse create(CityRequest request);
    CityResponse update(Long id, CityRequest request);
    void delete(Long id);
}

