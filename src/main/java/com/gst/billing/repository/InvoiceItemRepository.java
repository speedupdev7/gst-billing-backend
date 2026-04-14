package com.gst.billing.repository;

import com.gst.billing.entity.InvoiceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItemEntity, Long> {
    List<InvoiceItemEntity> findByInvoiceInvoiceIdAndIsDeletedFalse(Long invoiceId);
}
