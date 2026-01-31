package com.gst.masterdata.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "item_master")
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

    @Column(name = "hsn_code", length = 20)
    private String hsnCode;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "gst_rate")
    private BigDecimal gstRate;

    @Column(name = "price")
    private BigDecimal price;
}

