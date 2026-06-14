package com.gst.masterdata.repository;

import com.gst.masterdata.entity.ItemOpeningStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemOpeningStockRepository extends JpaRepository<ItemOpeningStockEntity, Long> {
    Optional<ItemOpeningStockEntity> findByItemItemIdAndIsDeletedFalse(Long itemId);
    Optional<ItemOpeningStockEntity> findByItemItemIdAndBatchCodeAndIsDeletedFalse(Long itemId, String batchCode);
    List<ItemOpeningStockEntity> findByItemItemIdInAndIsDeletedFalse(List<Long> itemIds);
}
