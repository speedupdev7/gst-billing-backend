package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateOpeningStockDTO {
    private Long itemId;
    private String itemName;
    private String batchCode;
    private Integer quantity;
    private BigDecimal purchaseRate;
    private BigDecimal sellingRate;
    private BigDecimal mrp;
    private BigDecimal gstPercent;
    private LocalDate expiryDate;
    private String supplierName;
    private String remarks;
}
