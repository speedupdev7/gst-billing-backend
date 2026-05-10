package com.gst.billing.repository;

import com.gst.billing.entity.InvoiceReturnItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceReturnItemRepository extends JpaRepository<InvoiceReturnItemEntity, Long> {
    List<InvoiceReturnItemEntity> findByInvoiceReturnReturnIdAndIsDeletedFalse(Long returnId);
    List<InvoiceReturnItemEntity> findByInvoiceReturnInvoiceInvoiceIdAndIsDeletedFalse(Long invoiceId);
    List<InvoiceReturnItemEntity> findByInvoiceItemInvoiceItemIdAndIsDeletedFalse(Long invoiceItemId);
}
