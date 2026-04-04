package com.gst.masterdata.controller;

import com.gst.masterdata.dto.DesignationRequest;
import com.gst.masterdata.dto.DesignationResponse;
import com.gst.masterdata.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/designations")
@RequiredArgsConstructor
public class DesignationController {

    private final DesignationService service;

    @GetMapping
    public Page<DesignationResponse> getAll(
            @RequestParam(required = false) String designation_name,
            Pageable pageable) {

        // Add default sorting by designation_id in descending order if no sort is specified
        if (pageable.getSort().isEmpty()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "designationId")
            );
        }

        return service.getAll(designation_name, pageable);
    }

    @GetMapping("/{id}")
    public DesignationResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DesignationResponse create(@RequestBody DesignationRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public DesignationResponse update(@PathVariable Long id, @RequestBody DesignationRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
