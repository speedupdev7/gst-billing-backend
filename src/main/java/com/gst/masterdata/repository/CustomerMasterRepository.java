package com.gst.masterdata.repository;

import com.gst.masterdata.entity.CustomerMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerMasterRepository extends JpaRepository<CustomerMasterEntity, Long> {

    List<CustomerMasterEntity> findTop20ByCustomerNameStartingWithIgnoreCaseAndIsDeletedFalse(String customerNamePrefix);
}
