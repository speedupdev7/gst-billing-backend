package com.gst.masterdata.repository;

import com.gst.masterdata.entity.SupplierMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierMasterRepository extends JpaRepository<SupplierMasterEntity, Long> {
}
