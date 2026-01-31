package com.gst.masterdata.repository;

import com.gst.masterdata.entity.UnitMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMasterRepository extends JpaRepository<UnitMasterEntity, Long> {
}
