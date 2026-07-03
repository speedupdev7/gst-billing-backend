package com.gst.billing.repository;

import com.gst.billing.entity.PurchaseRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRecordRepository extends JpaRepository<PurchaseRecordEntity, Long> {
    Optional<PurchaseRecordEntity> findByPurchaseNoAndIsDeletedFalse(String purchaseNo);
    List<PurchaseRecordEntity> findByPurchaseNoStartingWithIgnoreCaseAndIsDeletedFalse(String purchaseNoPrefix);
    List<PurchaseRecordEntity> findByIsDeletedFalse();
}
