package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OpeningStockItemDTO {
    private Long openingStockId;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String batchCode;
    private Integer openingStock;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal mrp;
    private BigDecimal totalAmount;
}
