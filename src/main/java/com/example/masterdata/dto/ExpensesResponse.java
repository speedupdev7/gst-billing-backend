package com.example.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpensesResponse {
    private Long expenseId;
    private String expenseCode;
    private String expenseName;
    private String description;
    private Boolean isReimbursable;
}
