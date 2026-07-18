package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GstSlabDTO {
    private String gstRate; // e.g., "5%" or "0%"
    private int invoices;
    private BigDecimal taxableValue;
    private BigDecimal gstAmount;
    private BigDecimal effectiveRate;
}
