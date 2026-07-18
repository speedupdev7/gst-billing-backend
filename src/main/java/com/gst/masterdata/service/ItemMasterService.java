package com.gst.masterdata.service;

import com.gst.masterdata.dto.ItemMasterDTO;
import com.gst.masterdata.dto.CreateOpeningStockDTO;
import com.gst.masterdata.dto.OpeningStockItemDTO;

import java.util.List;

public interface ItemMasterService {
    ItemMasterDTO createItem(ItemMasterDTO itemMasterDTO);
    ItemMasterDTO updateItem(Long itemId, ItemMasterDTO itemMasterDTO);
    ItemMasterDTO getItemById(Long itemId);
    List<ItemMasterDTO> getAllItems();
    List<ItemMasterDTO> searchItemsByNamePrefix(String itemNamePrefix);
    void deleteItem(Long itemId);
    com.gst.masterdata.dto.OpeningStockReportDTO getOpeningStockReport();
    OpeningStockItemDTO createOpeningStock(CreateOpeningStockDTO createOpeningStockDTO);
    com.gst.masterdata.dto.CurrentStockReportDTO getCurrentStockReport(java.time.LocalDate fromDate, java.time.LocalDate toDate, Long supplierId, String category);
}
