package com.gst.billing.repository;

import com.gst.billing.entity.InvoiceReturnEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceReturnRepository extends JpaRepository<InvoiceReturnEntity, Long> {
    Optional<InvoiceReturnEntity> findByReturnNoAndIsDeletedFalse(String returnNo);
    List<InvoiceReturnEntity> findByInvoiceInvoiceIdAndIsDeletedFalse(Long invoiceId);
    List<InvoiceReturnEntity> findByInvoiceInvoiceNoAndIsDeletedFalse(String invoiceNo);
}
