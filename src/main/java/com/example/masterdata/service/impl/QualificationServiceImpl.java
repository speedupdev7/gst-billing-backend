package com.example.masterdata.service.impl;

import com.example.masterdata.dto.QualificationRequest;
import com.example.masterdata.dto.QualificationResponse;
import com.example.masterdata.dto.RoleResponse;
import com.example.masterdata.entity.QualificationMasterEntity;
import com.example.masterdata.entity.RoleMasterEntity;
import com.example.masterdata.exceptions.BusinessException;
import com.example.masterdata.exceptions.ResourceNotFoundException;
import com.example.masterdata.repository.QualificationMasterRepository;
import com.example.masterdata.service.QualificationService;
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
public class QualificationServiceImpl implements QualificationService {

    private final QualificationMasterRepository repository;

    @Override
    public Page<QualificationResponse> getAll(String name, Pageable pageable) {
        Specification<QualificationMasterEntity> spec =
                (r, q, cb) -> cb.isFalse(r.get("isDeleted"));

        if (name != null)
            spec = spec.and((r, q, cb) ->
                    cb.like(cb.upper(r.get("qualificationName")), "%" + name.toUpperCase() + "%"));

        return repository.findAll(spec, pageable).map(this::map);
    }

    @Override
    public QualificationResponse getById(Long id) {
        return repository.findById(id)
                .filter(e -> !e.getIsDeleted())
                .map(this::map)
                .orElseThrow(() -> new EntityNotFoundException("Qualification not found"));
    }

    @Override
    public QualificationResponse create(QualificationRequest r) {
        QualificationMasterEntity e = new QualificationMasterEntity();
        e.setQualificationCode(r.getQualificationCode());
        e.setQualificationName(r.getQualificationName());
        e.setDescription(r.getDescription());
        return map(repository.save(e));
    }

    @Override
    public QualificationResponse update(Long id, QualificationRequest r) {
        QualificationMasterEntity e = repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Qualification not found with id: " + id));
        updateIfPresent(r.getQualificationCode(), e::setQualificationCode);
        updateIfPresent(r.getQualificationName(), e::setQualificationName);
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
        QualificationMasterEntity e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                 "Role not found with id: " + id));
        e.setIsDeleted(true);
        e.setDeletedAt(LocalDateTime.now());
    }

    private QualificationResponse map(QualificationMasterEntity e) {
        QualificationResponse r = new QualificationResponse();
        r.setQualificationId(e.getQualificationId());
        r.setQualificationName(e.getQualificationName());
        r.setIsActive(e.getIsActive());
        return r;
    }
}

