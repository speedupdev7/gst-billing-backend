package com.gst.billing.repository;

import com.gst.billing.entity.PurchaseReturnItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseReturnItemRepository extends JpaRepository<PurchaseReturnItemEntity, Long> {
    List<PurchaseReturnItemEntity> findByPurchaseReturnReturnIdAndIsDeletedFalse(Long returnId);
    List<PurchaseReturnItemEntity> findByPurchaseReturnPurchasePurchaseIdAndIsDeletedFalse(Long purchaseId);
    List<PurchaseReturnItemEntity> findByPurchaseItemPurchaseItemIdAndIsDeletedFalse(Long purchaseItemId);
}
