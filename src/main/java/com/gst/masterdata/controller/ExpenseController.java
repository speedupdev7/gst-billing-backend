package com.gst.masterdata.controller;

import com.gst.masterdata.dto.ExpensesRequest;
import com.gst.masterdata.dto.ExpensesResponse;
import com.gst.masterdata.service.ExpensesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpensesService service;

    @GetMapping
    public Page<ExpensesResponse> getExpenses(
            @RequestParam(required = false) String expense_code,
            @RequestParam(required = false) String expense_name,
            @RequestParam(required = false) Boolean is_active,
            Pageable pageable) {

        return service.getExpenses(expense_code, expense_name, is_active, pageable);
    }

    @GetMapping("/{id}")
    public ExpensesResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpensesResponse create(@RequestBody ExpensesRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ExpensesResponse update(@PathVariable Long id, @RequestBody ExpensesRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

