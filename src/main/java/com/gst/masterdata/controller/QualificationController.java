package com.gst.masterdata.controller;

import com.gst.masterdata.dto.QualificationRequest;
import com.gst.masterdata.dto.QualificationResponse;
import com.gst.masterdata.service.QualificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/qualifications")
@RequiredArgsConstructor
public class QualificationController {

    private final QualificationService service;

    @GetMapping
    public Page<QualificationResponse> getAll(
            @RequestParam(required = false) String qualification_name,
            Pageable pageable) {

        // Add default sorting by qualification_id in descending order if no sort is specified
        if (pageable.getSort().isEmpty()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "qualificationId")
            );
        }

        return service.getAll(qualification_name, pageable);
    }

    @GetMapping("/{id}")
    public QualificationResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QualificationResponse create(@RequestBody QualificationRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public QualificationResponse update(@PathVariable Long id, @RequestBody QualificationRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
