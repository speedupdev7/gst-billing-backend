package com.gst.billing.repository;

import com.gst.billing.entity.InvoiceReturnEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceReturnRepository extends JpaRepository<InvoiceReturnEntity, Long> {
    Optional<InvoiceReturnEntity> findByReturnNoAndIsDeletedFalse(String returnNo);
    List<InvoiceReturnEntity> findByInvoiceInvoiceIdAndIsDeletedFalse(Long invoiceId);
    List<InvoiceReturnEntity> findByInvoiceInvoiceNoAndIsDeletedFalse(String invoiceNo);

    @Query(value = "select r from InvoiceReturnEntity r " +
            "left join fetch r.invoice i " +
            "left join fetch i.customer c " +
            "where r.isDeleted = false " +
            "and (:fromDate is null or r.returnDate >= :fromDate) " +
            "and (:toDate is null or r.returnDate <= :toDate)",
            countQuery = "select count(r) from InvoiceReturnEntity r " +
                    "where r.isDeleted = false " +
                    "and (:fromDate is null or r.returnDate >= :fromDate) " +
                    "and (:toDate is null or r.returnDate <= :toDate)")
    Page<InvoiceReturnEntity> findByReturnDateRangePageable(@Param("fromDate") LocalDate fromDate,
                                                             @Param("toDate") LocalDate toDate,
                                                             Pageable pageable);
}
