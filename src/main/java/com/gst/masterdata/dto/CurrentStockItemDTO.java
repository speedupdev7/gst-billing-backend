package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CurrentStockItemDTO {
    private Long itemId;
    private String itemName;
    private String category; // optional
    private String unit;
    private Integer opening;
    private BigDecimal inward;
    private BigDecimal outward;
    private BigDecimal closing;
    private BigDecimal rate;
    private BigDecimal stockValue;
}
