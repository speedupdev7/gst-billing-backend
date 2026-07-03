package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PurchaseReturnDTO {
    private Long returnId;
    private String returnNo;
    private LocalDate returnDate;
    private Long purchaseId;
    private String purchaseNo;
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
    private List<PurchaseReturnItemDTO> items;
}
