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
import java.math.BigDecimal;
import java.util.ArrayList;
import com.gst.masterdata.dto.OpeningStockItemDTO;
import com.gst.masterdata.dto.OpeningStockReportDTO;
import com.gst.masterdata.dto.CreateOpeningStockDTO;

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
                .collect(Collectors.toMap(
                        record -> record.getItem().getItemId(),
                        record -> record,
                        (first, second) -> first
                ));

        return entities.stream().map(entity -> {
            ItemMasterDTO dto = new ItemMasterDTO();
            BeanUtils.copyProperties(entity, dto);
            applyOpeningStockToDto(dto, openingStockMap.get(entity.getItemId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemMasterDTO> searchItemsByNamePrefix(String itemNamePrefix) {
        List<ItemMasterEntity> entities = itemMasterRepository.findTop20ByItemNameContainingIgnoreCaseAndIsDeletedFalse(itemNamePrefix);
        List<Long> itemIds = entities.stream().map(ItemMasterEntity::getItemId).collect(Collectors.toList());
        Map<Long, ItemOpeningStockEntity> openingStockMap = itemOpeningStockRepository.findByItemItemIdInAndIsDeletedFalse(itemIds).stream()
                .collect(Collectors.toMap(
                        record -> record.getItem().getItemId(),
                        record -> record,
                        (first, second) -> first
                ));

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

    @Override
    public OpeningStockReportDTO getOpeningStockReport() {
        List<ItemOpeningStockEntity> allOpening = itemOpeningStockRepository.findAll();

        List<OpeningStockItemDTO> items = new ArrayList<>();
        for (ItemOpeningStockEntity o : allOpening) {
            if (o == null) continue;
            Boolean active = o.getIsActive();
            Boolean deleted = o.getIsDeleted();
            if (active == null || !active) continue;
            if (deleted != null && deleted) continue;

            OpeningStockItemDTO dto = new OpeningStockItemDTO();
            dto.setOpeningStockId(o.getOpeningStockId());
            if (o.getItem() != null) {
                dto.setItemId(o.getItem().getItemId());
                dto.setItemCode(o.getItem().getItemCode());
                dto.setItemName(o.getItem().getItemName());
            }
            dto.setBatchCode(o.getBatchCode());
            dto.setOpeningStock(o.getOpeningStock());
            dto.setPurchasePrice(o.getPurchasePrice());
            dto.setSalePrice(o.getSalePrice());
            dto.setMrp(o.getMrp());

            BigDecimal totalAmount = BigDecimal.ZERO;
            if (o.getPurchasePrice() != null && o.getOpeningStock() != null) {
                totalAmount = o.getPurchasePrice().multiply(new BigDecimal(o.getOpeningStock()));
            }
            dto.setTotalAmount(totalAmount);
            items.add(dto);
        }

        // calculate totals
        Long totalItems = items.stream()
                .map(OpeningStockItemDTO::getItemId)
                .filter(id -> id != null)
                .distinct()
                .count();

        Integer totalQuantity = items.stream()
                .map(OpeningStockItemDTO::getOpeningStock)
                .filter(q -> q != null)
                .reduce(0, Integer::sum);

        BigDecimal overallStockValue = items.stream()
                .map(OpeningStockItemDTO::getTotalAmount)
                .filter(t -> t != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OpeningStockReportDTO report = new OpeningStockReportDTO();
        report.setItems(items);
        report.setTotalItems(totalItems);
        report.setTotalQuantity(totalQuantity);
        report.setOverallStockValue(overallStockValue);
        return report;
    }

    @Override
    public OpeningStockItemDTO createOpeningStock(CreateOpeningStockDTO createOpeningStockDTO) {
        // Find item by itemId or itemName
        ItemMasterEntity itemEntity = null;
        
        if (createOpeningStockDTO.getItemId() != null) {
            itemEntity = itemMasterRepository.findById(createOpeningStockDTO.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + createOpeningStockDTO.getItemId()));
        } else if (createOpeningStockDTO.getItemName() != null && !createOpeningStockDTO.getItemName().isEmpty()) {
            List<ItemMasterEntity> items = itemMasterRepository.findTop20ByItemNameContainingIgnoreCaseAndIsDeletedFalse(createOpeningStockDTO.getItemName());
            if (items.isEmpty()) {
                throw new ResourceNotFoundException("Item not found with name: " + createOpeningStockDTO.getItemName());
            }
            itemEntity = items.get(0);
        } else {
            throw new IllegalArgumentException("Either itemId or itemName must be provided");
        }

        // Validate batch code presence
        if (createOpeningStockDTO.getBatchCode() == null || createOpeningStockDTO.getBatchCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Batch code is required to create opening stock");
        }

        // Check if opening stock already exists for this item and batch
        if (itemOpeningStockRepository
                .findByItemItemIdAndBatchCodeAndIsDeletedFalse(itemEntity.getItemId(), createOpeningStockDTO.getBatchCode().trim())
                .isPresent()) {
            throw new IllegalArgumentException("Opening stock already exists for item: " + itemEntity.getItemName() + " and batch: " + createOpeningStockDTO.getBatchCode());
        }

        // Create new opening stock record
        ItemOpeningStockEntity openingStockEntity = new ItemOpeningStockEntity();
        openingStockEntity.setItem(itemEntity);
        openingStockEntity.setBatchCode(createOpeningStockDTO.getBatchCode());
        openingStockEntity.setOpeningStock(createOpeningStockDTO.getQuantity());
        openingStockEntity.setPurchasePrice(createOpeningStockDTO.getPurchaseRate());
        openingStockEntity.setSalePrice(createOpeningStockDTO.getSellingRate());
        openingStockEntity.setMrp(createOpeningStockDTO.getMrp());
        openingStockEntity.setSupplierName(createOpeningStockDTO.getSupplierName());
        openingStockEntity.setRemarks(createOpeningStockDTO.getRemarks());
        openingStockEntity.setExpiryDate(createOpeningStockDTO.getExpiryDate());
        openingStockEntity.setIsActive(true);
        openingStockEntity.setIsDeleted(false);

        ItemOpeningStockEntity savedEntity = itemOpeningStockRepository.save(openingStockEntity);

        // Build response DTO
        OpeningStockItemDTO response = new OpeningStockItemDTO();
        response.setOpeningStockId(savedEntity.getOpeningStockId());
        response.setItemId(savedEntity.getItem().getItemId());
        response.setItemCode(savedEntity.getItem().getItemCode());
        response.setItemName(savedEntity.getItem().getItemName());
        response.setBatchCode(savedEntity.getBatchCode());
        response.setOpeningStock(savedEntity.getOpeningStock());
        response.setPurchasePrice(savedEntity.getPurchasePrice());
        response.setSalePrice(savedEntity.getSalePrice());
        response.setMrp(savedEntity.getMrp());

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (savedEntity.getPurchasePrice() != null && savedEntity.getOpeningStock() != null) {
            totalAmount = savedEntity.getPurchasePrice().multiply(new BigDecimal(savedEntity.getOpeningStock()));
        }
        response.setTotalAmount(totalAmount);

        return response;
    }

}
