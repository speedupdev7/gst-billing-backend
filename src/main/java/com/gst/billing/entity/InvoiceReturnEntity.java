package com.gst.billing.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice_return")
@Getter
@Setter
public class InvoiceReturnEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Long returnId;

    @Column(name = "return_no", nullable = false, unique = true)
    private String returnNo;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceRecordEntity invoice;

    @Column(name = "return_type")
    private String returnType;

    @Column(name = "reason_code")
    private String reasonCode;

    @Column(name = "reason_text")
    private String reasonText;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "total_gross_amt")
    private BigDecimal totalGrossAmount;

    @Column(name = "total_discount")
    private BigDecimal totalDiscount;

    @Column(name = "taxable_amount")
    private BigDecimal taxableAmount;

    @Column(name = "total_cgst")
    private BigDecimal totalCgst;

    @Column(name = "total_sgst")
    private BigDecimal totalSgst;

    @Column(name = "total_igst")
    private BigDecimal totalIgst;

    @Column(name = "round_off")
    private BigDecimal roundOff;

    @Column(name = "final_amount")
    private BigDecimal finalAmount;
}
