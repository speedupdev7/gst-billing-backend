package com.gst.billing.entity;

import com.gst.common.entity.BaseMasterEntity;
import com.gst.masterdata.entity.ItemMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item")
@Getter
@Setter
public class InvoiceItemEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_item_id")
    private Long invoiceItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceRecordEntity invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemMasterEntity item;

    @Column(name = "hsn_code")
    private String hsnCode;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @Column(name = "gross_amount")
    private BigDecimal grossAmount;

    @Column(name = "discount_pct")
    private BigDecimal discountPercentage;

    @Column(name = "discount_amt")
    private BigDecimal discountAmount;

    @Column(name = "taxable_amount")
    private BigDecimal taxableAmount;

    @Column(name = "gst_rate")
    private BigDecimal gstRate;

    @Column(name = "cgst_amt")
    private BigDecimal cgstAmount;

    @Column(name = "sgst_amt")
    private BigDecimal sgstAmount;

    @Column(name = "igst_amt")
    private BigDecimal igstAmount;

    @Column(name = "line_total")
    private BigDecimal lineTotal;
}

