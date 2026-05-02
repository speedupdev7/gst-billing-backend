package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.ItemMasterDTO;
import com.gst.masterdata.entity.ItemMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.ItemMasterRepository;
import com.gst.masterdata.service.ItemMasterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemMasterServiceImpl implements ItemMasterService {

    @Autowired
    private ItemMasterRepository itemMasterRepository;

    @Override
    public ItemMasterDTO createItem(ItemMasterDTO itemMasterDTO) {
        ItemMasterEntity entity = new ItemMasterEntity();
        BeanUtils.copyProperties(itemMasterDTO, entity);
        ItemMasterEntity savedEntity = itemMasterRepository.save(entity);
        ItemMasterDTO responseDTO = new ItemMasterDTO();
        BeanUtils.copyProperties(savedEntity, responseDTO);
        responseDTO.setBatchCode(savedEntity.getBatchCode());
        return responseDTO;
    }

    @Override
    public ItemMasterDTO updateItem(Long itemId, ItemMasterDTO itemMasterDTO) {
        ItemMasterEntity entity = itemMasterRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        BeanUtils.copyProperties(itemMasterDTO, entity, "itemId");
        ItemMasterEntity updatedEntity = itemMasterRepository.save(entity);
        ItemMasterDTO responseDTO = new ItemMasterDTO();
        BeanUtils.copyProperties(updatedEntity, responseDTO);
        responseDTO.setBatchCode(updatedEntity.getBatchCode());
        return responseDTO;
    }

    @Override
    public ItemMasterDTO getItemById(Long itemId) {
        ItemMasterEntity entity = itemMasterRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        ItemMasterDTO responseDTO = new ItemMasterDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        responseDTO.setBatchCode(entity.getBatchCode());
        return responseDTO;
    }

    @Override
    public List<ItemMasterDTO> getAllItems() {
        return itemMasterRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "itemId")).stream().map(entity -> {
            ItemMasterDTO dto = new ItemMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            dto.setBatchCode(entity.getBatchCode());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemMasterDTO> searchItemsByNamePrefix(String itemNamePrefix) {
        return itemMasterRepository.findTop20ByItemNameStartingWithIgnoreCaseAndIsDeletedFalse(itemNamePrefix).stream().map(entity -> {
            ItemMasterDTO dto = new ItemMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            dto.setBatchCode(entity.getBatchCode());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        ItemMasterEntity entity = itemMasterRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
    }

}
