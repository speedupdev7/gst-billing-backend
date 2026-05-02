package com.gst.masterdata.controller;

import com.gst.masterdata.dto.DepartmentRequest;
import com.gst.masterdata.dto.DepartmentResponse;
import com.gst.masterdata.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    @GetMapping
    public Page<DepartmentResponse> getDepartments(
            @RequestParam(required = false) String department_code,
            @RequestParam(required = false) String department_name,
            @RequestParam(required = false) Boolean is_active,
            Pageable pageable) {

        // Add default sorting by department_id in descending order if no sort is specified
        if (pageable.getSort().isEmpty()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "departmentId")
            );
        }

        return service.getDepartments(department_code, department_name, is_active, pageable);
    }

    @GetMapping("/{id}")
    public DepartmentResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@RequestBody DepartmentRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable Long id, @RequestBody DepartmentRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}