package com.gst.billing.entity;

import com.gst.common.entity.BaseMasterEntity;
import com.gst.masterdata.entity.ItemMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_return_item")
@Getter
@Setter
public class PurchaseReturnItemEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_item_id")
    private Long returnItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", nullable = false)
    private PurchaseReturnEntity purchaseReturn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_item_id", nullable = false)
    private PurchaseItemEntity purchaseItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemMasterEntity item;

    @Column(name = "batch_code")
    private String batchCode;

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
