package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.EmployeeRequest;
import com.gst.masterdata.dto.EmployeeResponse;
import com.gst.masterdata.entity.EmployeeMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.*;
import com.gst.masterdata.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMasterRepository repository;

    private final EmployeeMasterRepository employeeRepository;
    private final DesignationMasterRepository designationRepository;
    private final QualificationMasterRepository qualificationRepository;
    private final CityMasterRepository cityRepository;
    private final RoleMasterRepository roleRepository;

    @Override
    public Page<EmployeeResponse> getEmployees(
            String employeeCode, String lastName, Boolean isActive, Pageable pageable) {

        Specification<EmployeeMasterEntity> spec = (root, q, cb) -> cb.isFalse(root.get("isDeleted"));

        if (employeeCode != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("employeeCode")), "%" + employeeCode.toUpperCase() + "%"));

        if (lastName != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("lastName")), "%" + lastName.toUpperCase() + "%"));

        if (isActive != null)
            spec = spec.and((r, q, cb) -> cb.equal(r.get("isActive"), isActive));

        return repository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    @Override
    public EmployeeResponse getById(Long id) {
        return repository.findById(id)
                .filter(r -> !r.getIsDeleted())
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public EmployeeResponse create(EmployeeRequest req) {
        EmployeeMasterEntity e = new EmployeeMasterEntity();

        e.setEmployeeCode(req.getEmployeeCode());
        e.setFirstName(req.getFirstName());
        e.setMiddleName(req.getMiddleName());
        e.setLastName(req.getLastName());
        e.setGender(req.getGender());
        e.setDateOfBirth(req.getDateOfBirth());
        e.setJoiningDate(req.getJoiningDate());
        e.setMobileNo(req.getMobileNo());
        e.setEmailId(req.getEmailId());
        e.setPanNumber(req.getPanNumber());
        e.setAadhaarNumber(req.getAadhaarNumber());
        e.setAddress(req.getAddress());
        e.setIsActive(req.getIsActive()==null ? true : req.getIsActive());

        //set designation
        e.setDesignation(
                designationRepository.findById(req.getDesignationId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Designation not found: " + req.getDesignationId()))
        );

        //set qualification
        e.setQualification(
                qualificationRepository.findById(req.getQualifictionId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Qualification not found: " + req.getQualifictionId()))
        );

        //set city
        e.setCity(
                cityRepository.findById(req.getCityId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "City not found: " + req.getCityId()))
        );

        //set role
        e.setRole(
                roleRepository.findById(req.getRoleId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Role not found: " + req.getRoleId()))
        );

        return toResponse(repository.save(e));
    }

    @Override
    public EmployeeResponse update(Long id, EmployeeRequest req) {
        EmployeeMasterEntity e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "employee not found with id: " + id));
        updateIfPresent(req.getEmployeeCode(), e::setEmployeeCode);
        updateIfPresent(req.getFirstName(), e::setFirstName);
        updateIfPresent(req.getMiddleName(), e::setMiddleName);
        updateIfPresent(req.getLastName(), e::setLastName);
        updateIfPresent(req.getGender(), e::setGender);
        if (req.getDateOfBirth() != null) {
            e.setDateOfBirth(req.getDateOfBirth());
        }
        if (req.getJoiningDate() != null) {
            e.setJoiningDate(req.getJoiningDate());
        }
        updateIfPresent(req.getMobileNo(), e::setMobileNo);
        updateIfPresent(req.getEmailId(), e::setEmailId);
        updateIfPresent(req.getPanNumber(), e::setPanNumber);
        updateIfPresent(req.getAadhaarNumber(), e::setAadhaarNumber);
        updateIfPresent(req.getAddress(), e::setAddress);
        if (req.getIsActive() != null) {
            e.setIsActive(req.getIsActive());
        }
        return toResponse(repository.save(e));
    }

    private void updateIfPresent(String value, Consumer<String> setter) {
        if (StringUtils.hasText(value)) {
            setter.accept(value);
        }
    }

    @Override
    public void delete(Long id) {
        EmployeeMasterEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id));
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
    }

    private EmployeeResponse toResponse(EmployeeMasterEntity e) {
        EmployeeResponse r = new EmployeeResponse();

        r.setEmployeeId(e.getEmployeeId());
        r.setEmployeeCode(e.getEmployeeCode());
        r.setFirstName(e.getFirstName());
        r.setMiddleName(e.getMiddleName());
        r.setLastName(e.getLastName());
        if (e.getDesignation() != null) {
            r.setDesignationName(e.getDesignation().getDesignationName());
        }
        if (e.getQualification() != null) {
            r.setQualifictionId(e.getQualification().getQualificationId());
        }
        r.setGender(e.getGender());
        r.setDateOfBirth(e.getDateOfBirth());
        r.setJoiningDate(e.getJoiningDate());
        r.setMobileNo(e.getMobileNo());
        r.setEmailId(e.getEmailId());
        r.setPanNumber(e.getPanNumber());
        r.setAadhaarNumber(e.getAadhaarNumber());
        r.setAddress(e.getAddress());
        r.setIsActive(e.getIsActive());
        return r;
    }
}

