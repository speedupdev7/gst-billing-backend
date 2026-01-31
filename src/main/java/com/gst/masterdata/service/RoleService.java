package com.gst.masterdata.service;

import com.gst.masterdata.dto.RoleRequest;
import com.gst.masterdata.dto.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    Page<RoleResponse> getRoles(String roleCode, String roleName, Boolean isActive, Pageable pageable);
    RoleResponse getById(Long id);
    RoleResponse create(RoleRequest request);
    RoleResponse update(Long id, RoleRequest request);
    void delete(Long id);
}

