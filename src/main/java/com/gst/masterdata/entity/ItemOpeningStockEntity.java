package com.gst.masterdata.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "item_opening_stock", uniqueConstraints = {
        @UniqueConstraint(name = "uq_item_opening_stock_item_batch", columnNames = {"item_id", "batch_code"})
})
@Getter
@Setter
public class ItemOpeningStockEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opening_stock_id")
    private Long openingStockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemMasterEntity item;

    @Column(name = "batch_code")
    private String batchCode;

    @Column(name = "opening_stock")
    private Integer openingStock;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "mrp")
    private BigDecimal mrp;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}

