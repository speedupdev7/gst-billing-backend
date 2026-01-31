package com.gst.masterdata.repository;

import com.gst.masterdata.entity.ItemMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMasterRepository extends JpaRepository<ItemMasterEntity, Long> {
}
