package com.gst.masterdata.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "master_item")
@Getter @Setter
public class ItemMasterEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_code", nullable = false, unique = true)
    private String itemCode;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_name_details")
    private String itemNameDetails;

    @Column(name = "hsn_code", length = 20)
    private String hsnCode;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "gst_rate")
    private BigDecimal gstRate;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "mrp")
    private BigDecimal mrp;

    @Column(name = "opening_stock")
    private Integer openingStock;

    @Column(name = "batch_code")
    private String batchCode;
}
