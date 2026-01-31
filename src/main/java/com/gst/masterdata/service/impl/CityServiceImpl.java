package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.CityRequest;
import com.gst.masterdata.dto.CityResponse;
import com.gst.masterdata.entity.CityMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.CityMasterRepository;
import com.gst.masterdata.service.CityService;
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
public class CityServiceImpl implements CityService {

    private final CityMasterRepository repository;

    @Override
    public Page<CityResponse> getCities(
            String cityCode, String cityName, Boolean isActive, Pageable pageable) {

        Specification<CityMasterEntity> spec = (root, q, cb) -> cb.isFalse(root.get("isDeleted"));

        if (cityCode != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("cityCode")), "%" + cityCode.toUpperCase() + "%"));

        if (cityName != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("cityName")), "%" + cityName.toUpperCase() + "%"));

        if (isActive != null)
            spec = spec.and((r, q, cb) -> cb.equal(r.get("isActive"), isActive));

        return repository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    @Override
    public CityResponse getById(Long id) {
        return repository.findById(id)
                .filter(r -> !r.getIsDeleted())
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + id));
    }

    @Override
    public CityResponse create(CityRequest req) {
        CityMasterEntity e = new CityMasterEntity();
        e.setCityCode(req.getCityCode());
        e.setCityName(req.getCityName());
        e.setStateName(req.getStateName());
        e.setCountry(req.getCountry());
        e.setIsActive(req.getIsActive() != null ? req.getIsActive() :
                true);
        return toResponse(repository.save(e));
    }

    @Override
    public CityResponse update(Long id, CityRequest req) {
        CityMasterEntity e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "city not found with id: " + id));
        updateIfPresent(req.getCityCode(), e::setCityCode);
        updateIfPresent(req.getCityName(), e::setCityName);
        updateIfPresent(req.getStateName(), e::setStateName);
        updateIfPresent(req.getCountry(), e::setCountry);
        return toResponse(repository.save(e));
    }

    private void updateIfPresent(String value, Consumer<String> setter) {
        if (StringUtils.hasText(value)) {
            setter.accept(value);
        }
    }

    @Override
    public void delete(Long id) {
        CityMasterEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "City not found with id: " + id));
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
    }

    private CityResponse toResponse(CityMasterEntity e) {
        CityResponse r = new CityResponse();
        r.setCityId(e.getCityId());
        r.setCityCode(e.getCityCode());
        r.setCityName(e.getCityName());
        r.setStateName(e.getStateName());
        r.setCountry(e.getCountry());
        r.setIsActive(e.getIsActive());
        return r;
    }
}

