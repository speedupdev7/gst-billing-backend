package com.gst.masterdata.service;

import com.gst.masterdata.dto.ItemMasterDTO;

import java.util.List;

public interface ItemMasterService {
    ItemMasterDTO createItem(ItemMasterDTO itemMasterDTO);
    ItemMasterDTO updateItem(Long itemId, ItemMasterDTO itemMasterDTO);
    ItemMasterDTO getItemById(Long itemId);
    List<ItemMasterDTO> getAllItems();
    void deleteItem(Long itemId);
}
