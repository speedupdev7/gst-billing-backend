package com.gst.billing.repository;

import com.gst.billing.entity.InvoiceRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRecordRepository extends JpaRepository<InvoiceRecordEntity, Long> {
    Optional<InvoiceRecordEntity> findByInvoiceNoAndIsDeletedFalse(String invoiceNo);
    List<InvoiceRecordEntity> findByInvoiceNoStartingWithIgnoreCaseAndIsDeletedFalse(String invoiceNoPrefix);
    List<InvoiceRecordEntity> findByIsDeletedFalse();
}
