package com.gst.billing.repository;

import com.gst.billing.entity.PurchaseItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItemEntity, Long> {
    List<PurchaseItemEntity> findByPurchasePurchaseIdAndIsDeletedFalse(Long purchaseId);
}
