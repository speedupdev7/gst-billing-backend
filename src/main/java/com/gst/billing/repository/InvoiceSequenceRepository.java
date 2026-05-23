package com.gst.billing.repository;

import com.gst.billing.entity.InvoiceSequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequenceEntity, Long> {
    Optional<InvoiceSequenceEntity> findByFy(String fy);
}
