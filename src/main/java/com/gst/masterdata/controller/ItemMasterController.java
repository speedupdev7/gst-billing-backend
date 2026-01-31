package com.gst.masterdata.controller;

import com.gst.masterdata.dto.ItemMasterDTO;
import com.gst.masterdata.service.ItemMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemMasterService.deleteItem(itemId);
    }
}
