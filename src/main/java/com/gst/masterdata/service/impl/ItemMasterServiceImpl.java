package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.ItemMasterDTO;
import com.gst.masterdata.entity.ItemMasterEntity;
import com.gst.masterdata.entity.ItemOpeningStockEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.ItemOpeningStockRepository;
import com.gst.masterdata.repository.ItemMasterRepository;
import com.gst.masterdata.service.ItemMasterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemMasterServiceImpl implements ItemMasterService {

    @Autowired
    private ItemMasterRepository itemMasterRepository;

    @Autowired
    private ItemOpeningStockRepository itemOpeningStockRepository;

    @Override
    public ItemMasterDTO createItem(ItemMasterDTO itemMasterDTO) {
        ItemMasterEntity entity = new ItemMasterEntity();
        BeanUtils.copyProperties(itemMasterDTO, entity);
        ItemMasterEntity savedEntity = itemMasterRepository.save(entity);

        ItemOpeningStockEntity openingStockEntity = new ItemOpeningStockEntity();
        openingStockEntity.setItem(savedEntity);
        openingStockEntity.setBatchCode(itemMasterDTO.getBatchCode());
        openingStockEntity.setOpeningStock(itemMasterDTO.getOpeningStock());
        openingStockEntity.setPurchasePrice(itemMasterDTO.getPurchasePrice());
        openingStockEntity.setSalePrice(itemMasterDTO.getSalePrice());
        openingStockEntity.setMrp(itemMasterDTO.getMrp());
        itemOpeningStockRepository.save(openingStockEntity);

        ItemMasterDTO responseDTO = new ItemMasterDTO();
        BeanUtils.copyProperties(savedEntity, responseDTO);
        applyOpeningStockToDto(responseDTO, openingStockEntity);
        return responseDTO;
    }

    @Override
    public ItemMasterDTO updateItem(Long itemId, ItemMasterDTO itemMasterDTO) {
        ItemMasterEntity entity = itemMasterRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        BeanUtils.copyProperties(itemMasterDTO, entity, "itemId");
        ItemMasterEntity updatedEntity = itemMasterRepository.save(entity);

        ItemOpeningStockEntity openingStockEntity = itemOpeningStockRepository
                .findByItemItemIdAndIsDeletedFalse(itemId)
                .orElse(new ItemOpeningStockEntity());
        openingStockEntity.setItem(updatedEntity);
        openingStockEntity.setBatchCode(itemMasterDTO.getBatchCode());
        openingStockEntity.setOpeningStock(itemMasterDTO.getOpeningStock());
        openingStockEntity.setPurchasePrice(itemMasterDTO.getPurchasePrice());
        openingStockEntity.setSalePrice(itemMasterDTO.getSalePrice());
        openingStockEntity.setMrp(itemMasterDTO.getMrp());
        itemOpeningStockRepository.save(openingStockEntity);

        ItemMasterDTO responseDTO = new ItemMasterDTO();
        BeanUtils.copyProperties(updatedEntity, responseDTO);
        applyOpeningStockToDto(responseDTO, openingStockEntity);
        return responseDTO;
    }

    @Override
    public ItemMasterDTO getItemById(Long itemId) {
        ItemMasterEntity entity = itemMasterRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        ItemMasterDTO responseDTO = new ItemMasterDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        itemOpeningStockRepository.findByItemItemIdAndIsDeletedFalse(itemId)
                .ifPresent(openingStockEntity -> applyOpeningStockToDto(responseDTO, openingStockEntity));
        return responseDTO;
    }

    @Override
    public List<ItemMasterDTO> getAllItems() {
        List<ItemMasterEntity> entities = itemMasterRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "itemId"));
        List<Long> itemIds = entities.stream().map(ItemMasterEntity::getItemId).collect(Collectors.toList());
        Map<Long, ItemOpeningStockEntity> openingStockMap = itemOpeningStockRepository.findByItemItemIdInAndIsDeletedFalse(itemIds).stream()
                .collect(Collectors.toMap(record -> record.getItem().getItemId(), record -> record));

        return entities.stream().map(entity -> {
            ItemMasterDTO dto = new ItemMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            applyOpeningStockToDto(dto, openingStockMap.get(entity.getItemId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemMasterDTO> searchItemsByNamePrefix(String itemNamePrefix) {
        List<ItemMasterEntity> entities = itemMasterRepository.findTop20ByItemNameStartingWithIgnoreCaseAndIsDeletedFalse(itemNamePrefix);
        List<Long> itemIds = entities.stream().map(ItemMasterEntity::getItemId).collect(Collectors.toList());
        Map<Long, ItemOpeningStockEntity> openingStockMap = itemOpeningStockRepository.findByItemItemIdInAndIsDeletedFalse(itemIds).stream()
                .collect(Collectors.toMap(record -> record.getItem().getItemId(), record -> record));

        return entities.stream().map(entity -> {
            ItemMasterDTO dto = new ItemMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            applyOpeningStockToDto(dto, openingStockMap.get(entity.getItemId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        ItemMasterEntity entity = itemMasterRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());

        itemOpeningStockRepository.findByItemItemIdAndIsDeletedFalse(itemId).ifPresent(openingStockEntity -> {
            openingStockEntity.setIsDeleted(true);
            openingStockEntity.setDeletedAt(LocalDateTime.now());
            itemOpeningStockRepository.save(openingStockEntity);
        });
    }

    private void applyOpeningStockToDto(ItemMasterDTO dto, ItemOpeningStockEntity openingStockEntity) {
        if (openingStockEntity == null) {
            return;
        }
        dto.setBatchCode(openingStockEntity.getBatchCode());
        dto.setOpeningStock(openingStockEntity.getOpeningStock());
        dto.setPurchasePrice(openingStockEntity.getPurchasePrice());
        dto.setSalePrice(openingStockEntity.getSalePrice());
        dto.setMrp(openingStockEntity.getMrp());
    }

}
