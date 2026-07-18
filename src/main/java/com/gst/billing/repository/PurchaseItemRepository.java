package com.gst.billing.repository;

import com.gst.billing.entity.PurchaseItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItemEntity, Long> {
    List<PurchaseItemEntity> findByPurchasePurchaseIdAndIsDeletedFalse(Long purchaseId);

    @org.springframework.data.jpa.repository.Query("select coalesce(sum(pi.quantity),0) from PurchaseItemEntity pi " +
            "where pi.item.itemId = :itemId " +
            "and (:fromDate is null or pi.purchase.purchaseDate >= :fromDate) " +
            "and (:toDate is null or pi.purchase.purchaseDate <= :toDate) " +
            "and (:supplierId is null or pi.purchase.supplier.supplierId = :supplierId) " +
            "and pi.isDeleted = false and pi.purchase.isDeleted = false")
    java.math.BigDecimal sumQuantityByItemAndDateRange(@org.springframework.data.repository.query.Param("itemId") Long itemId,
                                                      @org.springframework.data.repository.query.Param("fromDate") java.time.LocalDate fromDate,
                                                      @org.springframework.data.repository.query.Param("toDate") java.time.LocalDate toDate,
                                                      @org.springframework.data.repository.query.Param("supplierId") Long supplierId);
}
