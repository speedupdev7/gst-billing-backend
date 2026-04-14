package com.gst.billing.repository;

import com.gst.billing.entity.InvoiceBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceBalanceRepository extends JpaRepository<InvoiceBalanceEntity, Long> {
    Optional<InvoiceBalanceEntity> findByInvoiceInvoiceIdAndIsDeletedFalse(Long invoiceId);
}
