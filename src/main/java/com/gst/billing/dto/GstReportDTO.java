package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class GstReportDTO {
    private BigDecimal taxableTurnover;
    private BigDecimal totalGstLiability;
    private int filedCount;
    private int pendingCount;
    private BigDecimal cgstCollected;
    private BigDecimal sgstCollected;
    private BigDecimal igstCollected;
    private List<GstSlabDTO> slabBreakup;
}
