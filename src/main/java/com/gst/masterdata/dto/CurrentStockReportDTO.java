package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CurrentStockReportDTO {
    private List<CurrentStockItemDTO> items;
    private int totalItems;
    private BigDecimal totalStockValue;
}
