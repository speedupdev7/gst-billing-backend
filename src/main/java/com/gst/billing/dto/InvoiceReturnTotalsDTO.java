package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceReturnTotalsDTO {
    private BigDecimal totalBase;
    private BigDecimal totalTax;
    private BigDecimal totalRefund;
}
