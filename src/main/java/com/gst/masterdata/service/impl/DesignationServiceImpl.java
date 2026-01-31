package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.DesignationRequest;
import com.gst.masterdata.dto.DesignationResponse;
import com.gst.masterdata.entity.DesignationMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.DesignationMasterRepository;
import com.gst.masterdata.service.DesignationService;
import jakarta.persistence.EntityNotFoundException;
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
        DesignationMasterEntity e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Designation not found with id: " + id));
        e.setDesignationName(r.getDesignationName());
        updateIfPresent(r.getDesignationCode(), e::setDesignationCode);
        updateIfPresent(r.getDesignationName(), e::setDesignationName);
        updateIfPresent(r.getDescription(), e::setDescription);
        return map(e);
    }

    private void updateIfPresent(String value, Consumer<String> setter) {
        if (StringUtils.hasText(value)) {
            setter.accept(value);
        }
    }

    @Override
    public void delete(Long id) {
        DesignationMasterEntity e = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Designation not found"));
        e.setIsDeleted(true);
        e.setDeletedAt(LocalDateTime.now());
    }

    private DesignationResponse map(DesignationMasterEntity e) {
        DesignationResponse r = new DesignationResponse();
        r.setDesignationId(e.getDesignationId());
        r.setDesignationCode(e.getDesignationCode()) ;
        r.setDesignationName(e.getDesignationName());
        r.setIsActive(e.getIsActive());
        return r;
    }
}

