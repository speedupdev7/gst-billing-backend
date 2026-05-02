package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.DepartmentRequest;
import com.gst.masterdata.dto.DepartmentResponse;
import com.gst.masterdata.entity.DepartmentMasterEntity;
import com.gst.masterdata.exceptions.BusinessException;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.DepartmentMasterRepository;
import com.gst.masterdata.service.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMasterRepository repository;

    @Override
    public Page<DepartmentResponse> getDepartments(
            String departmentCode, String departmentName, Boolean isActive, Pageable pageable) {

        Specification<DepartmentMasterEntity> spec = (root, q, cb) -> cb.isFalse(root.get("isDeleted"));

        if (departmentCode != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("departmentCode")), "%" + departmentCode.toUpperCase() + "%"));

        if (departmentName != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("departmentName")), "%" + departmentName.toUpperCase() + "%"));

        if (isActive != null)
            spec = spec.and((r, q, cb) -> cb.equal(r.get("isActive"), isActive));

        return repository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    @Override
    public DepartmentResponse getById(Long id) {
        return repository.findById(id)
                .filter(r -> !r.getIsDeleted())
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    @Override
    public DepartmentResponse create(DepartmentRequest req) {
        DepartmentMasterEntity e = new DepartmentMasterEntity();
        e.setDepartmentCode(req.getDepartmentCode());
        e.setDepartmentName(req.getDepartmentName());
        e.setDescription(req.getDescription());
        e.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
        e.setIsSystemDepartment(req.getIsSystemDepartment() != null ? req.getIsSystemDepartment() : false);
        return toResponse(repository.save(e));
    }

    @Override
    public DepartmentResponse update(Long id, DepartmentRequest req) {
        DepartmentMasterEntity e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Department not found with id: " + id));
        if(req.getDepartmentCode() != null && !req.getDepartmentCode().isEmpty()){
            e.setDepartmentCode(req.getDepartmentCode());
        }

        if(req.getDepartmentName() != null && !req.getDepartmentName().isEmpty()){
            e.setDepartmentName(req.getDepartmentName());
        }

        if(req.getDescription() != null){
            e.setDescription(req.getDescription());
        }

        if(req.getIsActive() != null){
            e.setIsActive(req.getIsActive());
        }

        if(req.getIsSystemDepartment() != null){
            e.setIsSystemDepartment(req.getIsSystemDepartment());
        }

        return toResponse(repository.save(e));
    }

    @Override
    public void delete(Long id) {
        DepartmentMasterEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + id));

        if (entity.getIsSystemDepartment()) {
            throw new BusinessException("System department cannot be deleted");
        }

        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
    }

    private DepartmentResponse toResponse(DepartmentMasterEntity e) {
        DepartmentResponse r = new DepartmentResponse();
        r.setDepartmentId(e.getDepartmentId());
        r.setDepartmentCode(e.getDepartmentCode());
        r.setDepartmentName(e.getDepartmentName());
        r.setIsActive(e.getIsActive());
        r.setDescription(e.getDescription());
        return r;
    }
}