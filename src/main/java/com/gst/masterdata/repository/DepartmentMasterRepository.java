package com.gst.masterdata.repository;

import com.gst.masterdata.entity.DepartmentMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentMasterRepository
        extends JpaRepository<DepartmentMasterEntity, Long>, JpaSpecificationExecutor<DepartmentMasterEntity> {
}