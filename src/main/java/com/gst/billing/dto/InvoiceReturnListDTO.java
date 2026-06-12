package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoiceReturnListDTO {
    private String returnNo;
    private String invoiceNo;
    private LocalDate returnDate;
    private String customerName;
    private String reasonCode;
    private BigDecimal finalAmount;
}
