package com.gst.masterdata.controller;

import com.gst.masterdata.dto.CityRequest;
import com.gst.masterdata.dto.CityResponse;
import com.gst.masterdata.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService service;

    @GetMapping
    public Page<CityResponse> getcities(
            @RequestParam(required = false) String city_code,
            @RequestParam(required = false) String city_name,
            @RequestParam(required = false) Boolean is_active,
            Pageable pageable) {

        // Add default sorting by city_id in descending order if no sort is specified
        if (pageable.getSort().isEmpty()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "cityId")
            );
        }

        return service.getCities(city_code, city_name, is_active, pageable);
    }

    @GetMapping("/{id}")
    public CityResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityResponse create(@RequestBody CityRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public CityResponse update(@PathVariable Long id, @RequestBody CityRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
