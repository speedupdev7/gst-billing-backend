package com.example.masterdata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "expenses_master")
@Getter @Setter
public class ExpenseMasterEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "expense_code", nullable = false, unique = true)
    private String expenseCode;

    @Column(name = "expense_name", nullable = false)
    private String expenseName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_reimbursable")
    private Boolean isReimbursable;
}

