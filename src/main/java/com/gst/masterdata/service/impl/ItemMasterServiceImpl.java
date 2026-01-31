package com.gst.masterdata.service.impl;

import com.gst.masterdata.dto.ItemMasterDTO;
import com.gst.masterdata.entity.ItemMasterEntity;
import com.gst.masterdata.repository.ItemMasterRepository;
import com.gst.masterdata.service.ItemMasterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return responseDTO;
    }

    @Override
    public ItemMasterDTO getItemById(Long itemId) {
        ItemMasterEntity entity = itemMasterRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        ItemMasterDTO responseDTO = new ItemMasterDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return responseDTO;
    }

    @Override
    public List<ItemMasterDTO> getAllItems() {
        return itemMasterRepository.findAll().stream().map(entity -> {
            ItemMasterDTO dto = new ItemMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        itemMasterRepository.deleteById(itemId);
    }
}
