package com.gst.masterdata.repository;

import com.gst.masterdata.entity.ExpenseMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseMasterRepository
        extends JpaRepository<ExpenseMasterEntity, Long>, JpaSpecificationExecutor<ExpenseMasterEntity> {
}

