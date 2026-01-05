package com.example.masterdata.repository;

import com.example.masterdata.entity.QualificationMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QualificationMasterRepository
        extends JpaRepository<QualificationMasterEntity, Long>, JpaSpecificationExecutor<QualificationMasterEntity> {
}

