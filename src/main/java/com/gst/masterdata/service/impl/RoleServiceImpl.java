package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.RoleRequest;
import com.gst.masterdata.dto.RoleResponse;
import com.gst.masterdata.entity.RoleMasterEntity;
import com.gst.masterdata.exceptions.BusinessException;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.RoleMasterRepository;
import com.gst.masterdata.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMasterRepository repository;

    @Override
    public Page<RoleResponse> getRoles(
            String roleCode, String roleName, Boolean isActive, Pageable pageable) {

        Specification<RoleMasterEntity> spec = (root, q, cb) -> cb.isFalse(root.get("isDeleted"));

        if (roleCode != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("roleCode")), "%" + roleCode.toUpperCase() + "%"));

        if (roleName != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("roleName")), "%" + roleName.toUpperCase() + "%"));

        if (isActive != null)
            spec = spec.and((r, q, cb) -> cb.equal(r.get("isActive"), isActive));

        return repository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    @Override
    public RoleResponse getById(Long id) {
        return repository.findById(id)
                .filter(r -> !r.getIsDeleted())
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    public RoleResponse create(RoleRequest req) {
        RoleMasterEntity e = new RoleMasterEntity();
        e.setRoleCode(req.getRoleCode());
        e.setRoleName(req.getRoleName());
        e.setDescription(req.getDescription());
        e.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
        e.setIsSystemRole(req.getIsSystemRole() != null ? req.getIsSystemRole() : false);
        return toResponse(repository.save(e));
    }

    @Override
    public RoleResponse update(Long id, RoleRequest req) {
        RoleMasterEntity e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Role not found with id: " + id));
        if(!req.getRoleCode().isEmpty()){
            e.setRoleCode(req.getRoleCode());
        }

        if(!req.getRoleName().isEmpty()){
            e.setRoleName(req.getRoleName());
        }

        if(req.getDescription() != null){
            e.setDescription(req.getDescription());
        }

        if(req.getIsActive() != null){
            e.setIsActive(req.getIsActive());
        }

        if(req.getIsSystemRole() != null){
            e.setIsSystemRole(req.getIsSystemRole());
        }

        return toResponse(repository.save(e));
    }

    @Override
    public void delete(Long id) {
        RoleMasterEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role not found with id: " + id));

        if (entity.getIsSystemRole()) {
            throw new BusinessException("System role cannot be deleted");
        }

        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
    }

    private RoleResponse toResponse(RoleMasterEntity e) {
        RoleResponse r = new RoleResponse();
        r.setRoleId(e.getRoleId());
        r.setRoleCode(e.getRoleCode());
        r.setRoleName(e.getRoleName());
        r.setIsActive(e.getIsActive());
        r.setDescription(e.getDescription());
        r.setRoleType(e.getIsSystemRole() ? "SYSTEM" : "NORMAL");
        return r;
    }
}

