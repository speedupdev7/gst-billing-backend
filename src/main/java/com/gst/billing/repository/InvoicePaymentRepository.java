package com.gst.billing.repository;

import com.gst.billing.entity.InvoicePaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoicePaymentRepository extends JpaRepository<InvoicePaymentEntity, Long> {
    List<InvoicePaymentEntity> findByInvoiceInvoiceIdAndIsDeletedFalse(Long invoiceId);
}
