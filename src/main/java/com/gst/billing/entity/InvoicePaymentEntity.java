package com.gst.billing.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice_payment")
@Getter
@Setter
public class InvoicePaymentEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceHeaderEntity invoice;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "reference_no")
    private String referenceNo;

    @Column(name = "payment_date")
    private LocalDate paymentDate;
}
