package com.gst.billing.repository;

import com.gst.billing.entity.PurchaseSequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseSequenceRepository extends JpaRepository<PurchaseSequenceEntity, Long> {
    Optional<PurchaseSequenceEntity> findByFy(String fy);
}
