package com.gst.billing.entity;

import com.gst.common.entity.BaseMasterEntity;
import com.gst.masterdata.entity.CustomerMasterEntity;
import com.gst.masterdata.entity.UnitMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "gst_adjustment_note")
@Getter
@Setter
public class GstAdjustmentNoteEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Long noteId;

    @Column(name = "note_type", nullable = false)
    private String noteType; // CREDIT / DEBIT

    @Column(name = "note_no", nullable = false, unique = true)
    private String noteNo;

    @Column(name = "note_date", nullable = false)
    private LocalDate noteDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_invoice_id", nullable = false)
    private InvoiceHeaderEntity originalInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitMasterEntity unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerMasterEntity customer;

    @Column(name = "reason_code")
    private String reasonCode;

    @Column(name = "reason_text")
    private String reasonText;

    @Column(name = "taxable_amount")
    private BigDecimal taxableAmount;

    @Column(name = "cgst_amount")
    private BigDecimal cgstAmount;

    @Column(name = "sgst_amount")
    private BigDecimal sgstAmount;

    @Column(name = "igst_amount")
    private BigDecimal igstAmount;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;
}
