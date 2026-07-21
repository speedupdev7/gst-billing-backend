package com.gst.billing.repository;

import com.gst.billing.entity.InvoiceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItemEntity, Long> {
    List<InvoiceItemEntity> findByInvoiceInvoiceIdAndIsDeletedFalse(Long invoiceId);

    @org.springframework.data.jpa.repository.Query("select coalesce(sum(ii.quantity),0) from InvoiceItemEntity ii " +
            "where ii.item.itemId = :itemId " +
            "and ii.invoice.invoiceDate >= coalesce(:fromDate, ii.invoice.invoiceDate) " +
            "and ii.invoice.invoiceDate <= coalesce(:toDate, ii.invoice.invoiceDate) " +
            "and ii.isDeleted = false and ii.invoice.isDeleted = false")
    java.math.BigDecimal sumQuantityByItemAndDateRange(@org.springframework.data.repository.query.Param("itemId") Long itemId,
                                                      @org.springframework.data.repository.query.Param("fromDate") java.time.LocalDate fromDate,
                                                      @org.springframework.data.repository.query.Param("toDate") java.time.LocalDate toDate);
}
