package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceItemDTO {
    private Long invoiceItemId;
    private Long itemId;
    private String batchCode;
    private String hsnCode;
    private BigDecimal quantity;
    private BigDecimal rate;
    private BigDecimal grossAmount;
    private BigDecimal discountPct;
    private BigDecimal discountAmt;
    private BigDecimal taxableAmount;
    private BigDecimal gstRate;
    private BigDecimal cgstAmt;
    private BigDecimal sgstAmt;
    private BigDecimal igstAmt;
    private BigDecimal lineTotal;
    private String itemName;
    private String itemCode;
    private String itemUnit;
}
