package com.gst.masterdata.controller;

import com.gst.masterdata.dto.ItemMasterDTO;
import com.gst.masterdata.dto.CreateOpeningStockDTO;
import com.gst.masterdata.dto.OpeningStockItemDTO;
import com.gst.masterdata.service.ItemMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/item-master")
public class ItemMasterController {

    @Autowired
    private ItemMasterService itemMasterService;

    @PostMapping
    public ItemMasterDTO createItem(@RequestBody ItemMasterDTO itemMasterDTO) {
        return itemMasterService.createItem(itemMasterDTO);
    }

    @PostMapping("/opening-stock")
    public OpeningStockItemDTO createOpeningStock(@RequestBody CreateOpeningStockDTO createOpeningStockDTO) {
        return itemMasterService.createOpeningStock(createOpeningStockDTO);
    }

    @PutMapping("/{itemId}")
    public ItemMasterDTO updateItem(@PathVariable Long itemId, @RequestBody ItemMasterDTO itemMasterDTO) {
        return itemMasterService.updateItem(itemId, itemMasterDTO);
    }

    @GetMapping("/{itemId}")
    public ItemMasterDTO getItemById(@PathVariable Long itemId) {
        return itemMasterService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemMasterDTO> getAllItems() {
        return itemMasterService.getAllItems();
    }

    @GetMapping("/opening-stock-report")
    public com.gst.masterdata.dto.OpeningStockReportDTO getOpeningStockReport() {
        return itemMasterService.getOpeningStockReport();
    }

        @GetMapping("/current-stock-report")
        public com.gst.masterdata.dto.CurrentStockReportDTO getCurrentStockReport(
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                @RequestParam(required = false) java.time.LocalDate fromDate,
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                @RequestParam(required = false) java.time.LocalDate toDate,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String category
        ) {
        return itemMasterService.getCurrentStockReport(fromDate, toDate, supplierId, category);
        }

    @GetMapping("/search")
    public List<ItemMasterDTO> searchItems(@RequestParam String q) {
        if (q == null || q.trim().length() < 3) {
            return Collections.emptyList();
        }
        return itemMasterService.searchItemsByNamePrefix(q.trim());
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemMasterService.deleteItem(itemId);
    }
}
