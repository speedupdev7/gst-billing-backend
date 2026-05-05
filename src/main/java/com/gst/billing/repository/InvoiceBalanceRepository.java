package com.gst.billing.repository;

import com.gst.billing.entity.InvoiceBalanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceBalanceRepository extends JpaRepository<InvoiceBalanceEntity, Long> {
    Optional<InvoiceBalanceEntity> findByInvoiceInvoiceIdAndIsDeletedFalse(Long invoiceId);

    @Query("SELECT ib FROM InvoiceBalanceEntity ib " +
           "JOIN FETCH ib.invoice ir " +
           "JOIN FETCH ir.unit u " +
           "WHERE ib.isDeleted = false " +
           "AND ir.invoiceDate >= :startDate " +
           "AND ir.invoiceDate <= :endDate " +
           "ORDER BY ir.invoiceDate DESC")
    List<InvoiceBalanceEntity> findInvoicesByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT ib FROM InvoiceBalanceEntity ib " +
           "JOIN FETCH ib.invoice ir " +
           "JOIN FETCH ir.unit u " +
           "WHERE ib.isDeleted = false " +
           "ORDER BY ir.invoiceDate DESC")
    List<InvoiceBalanceEntity> findAllInvoiceBalances();

    @Query("SELECT ib FROM InvoiceBalanceEntity ib " +
           "WHERE ib.isDeleted = false " +
           "AND ib.invoice.invoiceDate >= :startDate " +
           "AND ib.invoice.invoiceDate <= :endDate")
    Page<InvoiceBalanceEntity> findInvoicesByDateRangePageable(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT ib FROM InvoiceBalanceEntity ib " +
           "WHERE ib.isDeleted = false")
    Page<InvoiceBalanceEntity> findAllInvoiceBalancesPageable(Pageable pageable);
}
