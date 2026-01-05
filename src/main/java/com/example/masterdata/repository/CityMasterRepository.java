package com.example.masterdata.repository;

import com.example.masterdata.entity.CityMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CityMasterRepository
        extends JpaRepository<CityMasterEntity, Long>, JpaSpecificationExecutor<CityMasterEntity> {
}

