package com.example.masterdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.example.masterdata.entity.DesignationMasterEntity;

@Repository
public interface DesignationMasterRepository
        extends JpaRepository<DesignationMasterEntity, Long>, JpaSpecificationExecutor<DesignationMasterEntity> {
}

