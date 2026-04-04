package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemMasterDTO {
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String itemNameDetails;
    private String hsnCode;
    private String unit;
    private BigDecimal gstRate;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal mrp;
    private Integer openingStock;
}
