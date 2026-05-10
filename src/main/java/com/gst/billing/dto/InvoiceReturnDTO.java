package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceReturnDTO {
    private Long returnId;
    private String returnNo;
    private LocalDate returnDate;
    private Long invoiceId;
    private String invoiceNo;
    private String returnType;
    private String reasonCode;
    private String reasonText;
    private String remarks;
    private BigDecimal totalGrossAmount;
    private BigDecimal totalDiscount;
    private BigDecimal taxableAmount;
    private BigDecimal totalCgst;
    private BigDecimal totalSgst;
    private BigDecimal totalIgst;
    private BigDecimal roundOff;
    private BigDecimal finalAmount;
    private List<InvoiceReturnItemDTO> items;
}
