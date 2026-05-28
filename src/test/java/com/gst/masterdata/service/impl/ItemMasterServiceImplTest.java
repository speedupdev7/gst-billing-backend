package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.ItemMasterDTO;
import com.gst.masterdata.entity.ItemMasterEntity;
import com.gst.masterdata.entity.ItemOpeningStockEntity;
import com.gst.masterdata.repository.ItemOpeningStockRepository;
import com.gst.masterdata.repository.ItemMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemMasterServiceImplTest {

    @Mock
    private ItemMasterRepository itemMasterRepository;

    @Mock
    private ItemOpeningStockRepository itemOpeningStockRepository;

    @InjectMocks
    private ItemMasterServiceImpl service;

    private ItemMasterDTO dto;
    private ItemMasterEntity entity;
    private ItemOpeningStockEntity openingStockEntity;

    @BeforeEach
    void setUp() {
        dto = new ItemMasterDTO();
        dto.setItemName("Test Item");
        dto.setItemCode("ITEM01");
        dto.setHsnCode("1001");
        dto.setUnit("KG");
        dto.setGstRate(BigDecimal.valueOf(18));
        dto.setOpeningStock(10);
        dto.setBatchCode("BATCH01");
        dto.setPurchasePrice(BigDecimal.valueOf(100));
        dto.setSalePrice(BigDecimal.valueOf(120));
        dto.setMrp(BigDecimal.valueOf(150));

        entity = new ItemMasterEntity();
        entity.setItemId(1L);
        entity.setItemName(dto.getItemName());
        entity.setItemCode(dto.getItemCode());
        entity.setHsnCode(dto.getHsnCode());
        entity.setUnit(dto.getUnit());
        entity.setGstRate(dto.getGstRate());

        openingStockEntity = new ItemOpeningStockEntity();
        openingStockEntity.setOpeningStockId(1L);
        openingStockEntity.setBatchCode(dto.getBatchCode());
        openingStockEntity.setOpeningStock(dto.getOpeningStock());
        openingStockEntity.setPurchasePrice(dto.getPurchasePrice());
        openingStockEntity.setSalePrice(dto.getSalePrice());
        openingStockEntity.setMrp(dto.getMrp());
        openingStockEntity.setItem(entity);
    }

    @Test
    void createItem_shouldReturnSavedDto() {
        when(itemMasterRepository.save(any(ItemMasterEntity.class))).thenReturn(entity);
        when(itemOpeningStockRepository.save(any(ItemOpeningStockEntity.class))).thenReturn(openingStockEntity);

        ItemMasterDTO result = service.createItem(dto);

        assertNotNull(result);
        assertEquals(1L, result.getItemId());
        assertEquals("Test Item", result.getItemName());
        assertEquals(10, result.getOpeningStock());
        assertEquals("BATCH01", result.getBatchCode());
        assertEquals(BigDecimal.valueOf(100), result.getPurchasePrice());
        verify(itemMasterRepository).save(any(ItemMasterEntity.class));
        verify(itemOpeningStockRepository).save(any(ItemOpeningStockEntity.class));
    }

    @Test
    void getItemById_shouldReturnDto_whenFound() {
        when(itemMasterRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(itemOpeningStockRepository.findByItemItemIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(openingStockEntity));

        ItemMasterDTO result = service.getItemById(1L);

        assertEquals("Test Item", result.getItemName());
        assertEquals(10, result.getOpeningStock());
        assertEquals("BATCH01", result.getBatchCode());
        verify(itemMasterRepository).findById(1L);
        verify(itemOpeningStockRepository).findByItemItemIdAndIsDeletedFalse(1L);
    }

    @Test
    void getItemById_shouldThrow_whenNotFound() {
        when(itemMasterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getItemById(1L));
    }

    @Test
    void getAllItems_shouldReturnList() {
        when(itemMasterRepository.findAll(any(Sort.class))).thenReturn(List.of(entity));
        when(itemOpeningStockRepository.findByItemItemIdInAndIsDeletedFalse(any())).thenReturn(List.of(openingStockEntity));

        List<ItemMasterDTO> result = service.getAllItems();

        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getItemName());
        assertEquals(10, result.get(0).getOpeningStock());
        verify(itemMasterRepository).findAll(eq(Sort.by(Direction.DESC, "itemId")));
        verify(itemOpeningStockRepository).findByItemItemIdInAndIsDeletedFalse(any());
    }

    @Test
    void deleteItem_shouldSoftDeleteEntity() {
        when(itemMasterRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(itemOpeningStockRepository.findByItemItemIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(openingStockEntity));

        service.deleteItem(1L);

        assertTrue(entity.getIsDeleted());
        assertNotNull(entity.getDeletedAt());
        assertTrue(openingStockEntity.getIsDeleted());
        assertNotNull(openingStockEntity.getDeletedAt());
        verify(itemMasterRepository, never()).deleteById(any());
        verify(itemOpeningStockRepository).save(openingStockEntity);
    }
}
