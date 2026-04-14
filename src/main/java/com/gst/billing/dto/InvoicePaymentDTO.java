package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoicePaymentDTO {
    private Long paymentId;
    private String paymentMode;
    private BigDecimal amount;
    private String referenceNo;
    private LocalDate paymentDate;
}
