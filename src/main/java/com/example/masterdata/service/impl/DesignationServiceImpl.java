package com.example.masterdata.service.impl;

import com.example.masterdata.dto.DesignationRequest;
import com.example.masterdata.dto.DesignationResponse;
import com.example.masterdata.entity.DesignationMasterEntity;
import com.example.masterdata.repository.DesignationMasterRepository;
import com.example.masterdata.service.DesignationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {

    private final DesignationMasterRepository repository;

    @Override
    public Page<DesignationResponse> getAll(String name, Pageable pageable) {
        Specification<DesignationMasterEntity> spec =
                (r, q, cb) -> cb.isFalse(r.get("isDeleted"));

        if (name != null)
            spec = spec.and((r, q, cb) ->
                    cb.like(cb.upper(r.get("designationName")), "%" + name.toUpperCase() + "%"));

        return repository.findAll(spec, pageable).map(this::map);
    }

    @Override
    public DesignationResponse getById(Long id) {
        return repository.findById(id)
                .filter(e -> !e.getIsDeleted())
                .map(this::map)
                .orElseThrow(() -> new EntityNotFoundException("Designation not found"));
    }

    @Override
    public DesignationResponse create(DesignationRequest r) {
        DesignationMasterEntity e = new DesignationMasterEntity();
        e.setDesignationCode(r.getDesignationCode());
        e.setDesignationName(r.getDesignationName());
        return map(repository.save(e));
    }

    @Override
    public DesignationResponse update(Long id, DesignationRequest r) {
        DesignationMasterEntity e = repository.findById(id).orElseThrow();
        e.setDesignationName(r.getDesignationName());
        return map(e);
    }

    @Override
    public void delete(Long id) {
        DesignationMasterEntity e = repository.findById(id).orElseThrow();
        e.setIsDeleted(true);
        e.setDeletedAt(LocalDateTime.now());
    }

    private DesignationResponse map(DesignationMasterEntity e) {
        DesignationResponse r = new DesignationResponse();
        r.setDesignationId(e.getDesignationId());
        r.setDesignationName(e.getDesignationName());
        r.setIsActive(e.getIsActive());
        return r;
    }
}

