package com.gst.masterdata.repository;

import com.gst.masterdata.entity.EmployeeMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeMasterRepository
        extends JpaRepository<EmployeeMasterEntity, Long>, JpaSpecificationExecutor<EmployeeMasterEntity> {
}

