package com.example.masterdata.controller;

import com.example.masterdata.dto.QualificationRequest;
import com.example.masterdata.dto.QualificationResponse;
import com.example.masterdata.service.QualificationService;
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

