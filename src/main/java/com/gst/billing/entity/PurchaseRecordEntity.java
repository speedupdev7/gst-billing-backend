package com.gst.billing.entity;

import com.gst.common.entity.BaseMasterEntity;
import com.gst.masterdata.entity.SupplierMasterEntity;
import com.gst.masterdata.entity.UnitMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchase_record")
@Getter
@Setter
public class PurchaseRecordEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long purchaseId;

    @Column(name = "purchase_no", nullable = false, unique = true)
    private String purchaseNo;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitMasterEntity unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierMasterEntity supplier;

    @Column(name = "place_of_supply")
    private String placeOfSupply;

    @Column(name = "state_code")
    private String stateCode;

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

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;

    @Column(name = "transporter_name")
    private String transporterName;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "narration")
    private String narration;
}
