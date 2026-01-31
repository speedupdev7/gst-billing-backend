package com.gst.masterdata.controller;

import com.gst.masterdata.dto.EmployeeRequest;
import com.gst.masterdata.dto.EmployeeResponse;
import com.gst.masterdata.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping
    public Page<EmployeeResponse> getEmployees(
            @RequestParam(required = false) String employee_code,
            @RequestParam(required = false) String employee_name,
            @RequestParam(required = false) Boolean is_active,
            Pageable pageable) {

        return service.getEmployees(employee_code, employee_name, is_active, pageable);
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@RequestBody EmployeeRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @RequestBody EmployeeRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

