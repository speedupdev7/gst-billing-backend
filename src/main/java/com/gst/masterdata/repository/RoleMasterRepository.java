package com.gst.masterdata.repository;

import com.gst.masterdata.entity.RoleMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMasterRepository
        extends JpaRepository<RoleMasterEntity, Long>, JpaSpecificationExecutor<RoleMasterEntity> {
}

