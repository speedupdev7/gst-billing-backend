package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BillingReportPagedDTO {
    private PagedResponse<InvoiceRecordDTO> invoicesPage;
    private BigDecimal totalGross;
    private BigDecimal totalDiscount;
    private BigDecimal totalGst;
    private BigDecimal totalNet;
}
