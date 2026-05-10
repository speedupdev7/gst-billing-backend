package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceRecordDTO {
    private Long invoiceId;
    private String invoiceNo;
    private LocalDate invoiceDate;
    private Long unitId;
    private Long customerId;
    private String placeOfSupply;
    private String stateCode;
    private Boolean reverseCharge;
    private BigDecimal totalGrossAmount;
    private BigDecimal totalDiscount;
    private BigDecimal taxableAmount;
    private BigDecimal totalCgst;
    private BigDecimal totalSgst;
    private BigDecimal totalIgst;
    private BigDecimal roundOff;
    private BigDecimal finalAmount;
    private String transporterName;
    private String customerName;
    private String unitName;
    private String vehicleNumber;
    private String narration;
    private List<InvoiceItemDTO> items;
    private InvoiceBalanceDTO balance;
    private List<InvoicePaymentDTO> payments;
    private BigDecimal totalReturnAmount;
    private BigDecimal totalReturnCgst;
    private BigDecimal totalReturnSgst;
    private BigDecimal totalReturnIgst;
    private BigDecimal totalReturnTaxableAmount;
    private List<InvoiceReturnDTO> returns;
}
