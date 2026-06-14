package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OpeningStockReportDTO {
    private List<OpeningStockItemDTO> items;
    private Long totalItems;
    private Integer totalQuantity;
    private BigDecimal overallStockValue;
}
