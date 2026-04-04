package com.gst.masterdata.controller;

import com.gst.masterdata.dto.RoleRequest;
import com.gst.masterdata.dto.RoleResponse;
import com.gst.masterdata.service.RoleService;
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

        // Add default sorting by role_id in descending order if no sort is specified
        if (pageable.getSort().isEmpty()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "roleId")
            );
        }

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
