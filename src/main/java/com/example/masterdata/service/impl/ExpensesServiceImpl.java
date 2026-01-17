package com.example.masterdata.service.impl;

import com.example.masterdata.dto.ExpensesRequest;
import com.example.masterdata.dto.ExpensesResponse;
import com.example.masterdata.entity.ExpenseMasterEntity;
import com.example.masterdata.exceptions.ResourceNotFoundException;
import com.example.masterdata.repository.ExpenseMasterRepository;
import com.example.masterdata.service.ExpensesService;
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
public class ExpensesServiceImpl implements ExpensesService {

    private final ExpenseMasterRepository repository;

    @Override
    public Page<ExpensesResponse> getExpenses(
            String expenseCode, String expenseName, Boolean isActive, Pageable pageable) {

        Specification<ExpenseMasterEntity> spec = (root, q, cb) -> cb.isFalse(root.get("isDeleted"));

        if (expenseCode != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("expenseCode")), "%" + expenseCode.toUpperCase() + "%"));

        if (expenseName != null)
            spec = spec.and((r, q, cb) -> cb.like(cb.upper(r.get("expenseName")), "%" + expenseName.toUpperCase() + "%"));

        if (isActive != null)
            spec = spec.and((r, q, cb) -> cb.equal(r.get("isActive"), isActive));

        return repository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    @Override
    public ExpensesResponse getById(Long id) {
        return repository.findById(id)
                .filter(r -> !r.getIsDeleted())
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
    }

    @Override
    public ExpensesResponse create(ExpensesRequest req) {
        ExpenseMasterEntity e = new ExpenseMasterEntity();
        e.setExpenseCode(req.getExpenseCode());
        e.setExpenseName(req.getExpenseName());
        e.setDescription(req.getDescription());
        e.setIsReimbursable(req.getIsReimbursable());
        return toResponse(repository.save(e));
    }

    @Override
    public ExpensesResponse update(Long id, ExpensesRequest req) {
        ExpenseMasterEntity e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found with id: " + id));
        updateIfPresent(req.getExpenseCode(), e::setExpenseCode);
        updateIfPresent(req.getExpenseName(), e::setExpenseName);
        updateIfPresent(req.getDescription(), e::setDescription);
        if (req.getIsReimbursable() != null) {
            e.setIsReimbursable(req.getIsReimbursable());
        }
        return toResponse(repository.save(e));
    }

    private void updateIfPresent(String value, Consumer<String> setter) {
        if (StringUtils.hasText(value)) {
            setter.accept(value);
        }
    }

    @Override
    public void delete(Long id) {
        ExpenseMasterEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found with id: " + id));
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
    }

    private ExpensesResponse toResponse(ExpenseMasterEntity e) {
        ExpensesResponse r = new ExpensesResponse();
        r.setExpenseId(e.getExpenseId());
        r.setExpenseCode(e.getExpenseCode());
        r.setExpenseName(e.getExpenseName());
        r.setDescription(e.getDescription());
        r.setIsReimbursable(e.getIsReimbursable());
        return r;
    }
}

