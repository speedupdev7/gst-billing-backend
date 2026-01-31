package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpensesRequest {
    private String expenseCode;
    private String expenseName;
    private String description;
    private Boolean isReimbursable;
}
