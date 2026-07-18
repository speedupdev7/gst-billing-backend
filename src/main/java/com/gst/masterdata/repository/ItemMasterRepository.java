package com.gst.masterdata.repository;

import com.gst.masterdata.entity.ItemMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemMasterRepository extends JpaRepository<ItemMasterEntity, Long> {

    List<ItemMasterEntity> findTop20ByItemNameContainingIgnoreCaseAndIsDeletedFalse(String itemNameSubstring);

    List<ItemMasterEntity> findByIsDeletedFalse();
}
