package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.ItemMasterDTO;
import com.gst.masterdata.entity.ItemMasterEntity;
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

    @InjectMocks
    private ItemMasterServiceImpl service;

    private ItemMasterDTO dto;
    private ItemMasterEntity entity;

    @BeforeEach
    void setUp() {
        dto = new ItemMasterDTO();
        dto.setItemName("Test Item");
        dto.setItemCode("ITEM01");
        dto.setHsnCode("1001");
        dto.setUnit("KG");
        dto.setGstRate(BigDecimal.valueOf(18));

        entity = new ItemMasterEntity();
        entity.setItemId(1L);
        entity.setItemName(dto.getItemName());
        entity.setItemCode(dto.getItemCode());
        entity.setHsnCode(dto.getHsnCode());
        entity.setUnit(dto.getUnit());
        entity.setGstRate(dto.getGstRate());
    }

    @Test
    void createItem_shouldReturnSavedDto() {
        when(itemMasterRepository.save(any(ItemMasterEntity.class))).thenReturn(entity);

        ItemMasterDTO result = service.createItem(dto);

        assertNotNull(result);
        assertEquals(1L, result.getItemId());
        assertEquals("Test Item", result.getItemName());
        verify(itemMasterRepository).save(any(ItemMasterEntity.class));
    }

    @Test
    void getItemById_shouldReturnDto_whenFound() {
        when(itemMasterRepository.findById(1L)).thenReturn(Optional.of(entity));

        ItemMasterDTO result = service.getItemById(1L);

        assertEquals("Test Item", result.getItemName());
        verify(itemMasterRepository).findById(1L);
    }

    @Test
    void getItemById_shouldThrow_whenNotFound() {
        when(itemMasterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getItemById(1L));
    }

    @Test
    void getAllItems_shouldReturnList() {
        when(itemMasterRepository.findAll(any(Sort.class))).thenReturn(List.of(entity));

        List<ItemMasterDTO> result = service.getAllItems();

        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getItemName());
        verify(itemMasterRepository).findAll(eq(Sort.by(Direction.DESC, "itemId")));
    }

    @Test
    void deleteItem_shouldCallRepositoryDeleteById() {
        service.deleteItem(1L);

        verify(itemMasterRepository).deleteById(1L);
    }
}
