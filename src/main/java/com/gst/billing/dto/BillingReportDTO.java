package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BillingReportDTO {
    private List<InvoiceRecordDTO> invoices;
    private BigDecimal totalGross;
    private BigDecimal totalDiscount;
    private BigDecimal totalGst;
    private BigDecimal totalNet;
}
