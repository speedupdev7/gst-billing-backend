package com.gst.billing.repository;

import com.gst.billing.entity.PurchaseReturnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseReturnRepository extends JpaRepository<PurchaseReturnEntity, Long> {
    Optional<PurchaseReturnEntity> findByReturnNoAndIsDeletedFalse(String returnNo);
    List<PurchaseReturnEntity> findByPurchasePurchaseIdAndIsDeletedFalse(Long purchaseId);
    List<PurchaseReturnEntity> findByPurchasePurchaseNoAndIsDeletedFalse(String purchaseNo);
}
