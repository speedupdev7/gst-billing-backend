package com.gst.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceBalanceDetailDTO {
    private Long balanceId;           // primary id from invoice_balance
    private String invoiceNo;         // invoice number from invoice_record
    private LocalDate invoiceDate;    // invoice date from invoice_record
    private BigDecimal invoiceAmount; // invoice amount from invoice_balance
    private String status;            // status from invoice_balance
    private String unitName; 
    private Long customerId;
    private String customerName;
    private BigDecimal paidAmount;    // unit name from unit_master
    private BigDecimal pendingAmount; // invoice amount from invoice_balance
}
