package com.example.masterdata.controller;

import com.example.masterdata.dto.RoleRequest;
import com.example.masterdata.dto.RoleResponse;
import com.example.masterdata.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @GetMapping
    public Page<RoleResponse> getRoles(
            @RequestParam(required = false) String role_code,
            @RequestParam(required = false) String role_name,
            @RequestParam(required = false) Boolean is_active,
            Pageable pageable) {

        return service.getRoles(role_code, role_name, is_active, pageable);
    }

    @GetMapping("/{id}")
    public RoleResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@RequestBody RoleRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable Long id, @RequestBody RoleRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

