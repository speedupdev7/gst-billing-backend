package com.gst.billing.service.impl;

import com.gst.billing.dto.PagedResponse;
import com.gst.billing.dto.PurchaseItemDTO;
import com.gst.billing.dto.PurchaseRecordDTO;
import com.gst.billing.dto.PurchaseReturnDTO;
import com.gst.billing.dto.PurchaseReturnItemDTO;
import com.gst.billing.dto.PurchaseReturnItemRequestDTO;
import com.gst.billing.dto.PurchaseReturnRequestDTO;
import com.gst.billing.entity.PurchaseItemEntity;
import com.gst.billing.entity.PurchaseRecordEntity;
import com.gst.billing.entity.PurchaseReturnEntity;
import com.gst.billing.entity.PurchaseReturnItemEntity;
import com.gst.billing.entity.PurchaseSequenceEntity;
import com.gst.billing.repository.PurchaseItemRepository;
import com.gst.billing.repository.PurchaseRecordRepository;
import com.gst.billing.repository.PurchaseReturnItemRepository;
import com.gst.billing.repository.PurchaseReturnRepository;
import com.gst.billing.repository.PurchaseSequenceRepository;
import com.gst.billing.service.PurchaseService;
import com.gst.masterdata.entity.ItemMasterEntity;
import com.gst.masterdata.entity.SupplierMasterEntity;
import com.gst.masterdata.entity.UnitMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.ItemMasterRepository;
import com.gst.masterdata.repository.ItemOpeningStockRepository;
import com.gst.masterdata.repository.SupplierMasterRepository;
import com.gst.masterdata.repository.UnitMasterRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final int MAX_SEQUENCE_RETRIES = 8;

    @Autowired
    private PurchaseRecordRepository purchaseRecordRepository;

    @Autowired
    private PurchaseItemRepository purchaseItemRepository;

    @Autowired
    private PurchaseReturnRepository purchaseReturnRepository;

    @Autowired
    private PurchaseReturnItemRepository purchaseReturnItemRepository;

    @Autowired
    private PurchaseSequenceRepository purchaseSequenceRepository;

    @Autowired
    private ItemMasterRepository itemMasterRepository;

    @Autowired
    private ItemOpeningStockRepository itemOpeningStockRepository;

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Autowired
    private UnitMasterRepository unitMasterRepository;

    @Override
    @Transactional
    public PurchaseRecordDTO createPurchase(PurchaseRecordDTO purchaseRecordDTO) {
        validatePurchaseRequest(purchaseRecordDTO);

        PurchaseRecordEntity purchaseEntity = toPurchaseRecordEntity(purchaseRecordDTO);
        String purchaseNo = generateNextPurchaseNo(purchaseEntity.getPurchaseDate());
        purchaseEntity.setPurchaseNo(purchaseNo);

        PurchaseRecordEntity savedPurchase = purchaseRecordRepository.save(purchaseEntity);
        List<PurchaseItemEntity> savedItems = savePurchaseItems(savedPurchase, purchaseRecordDTO.getItems());

        savedItems.forEach(this::updateOpeningStockForPurchaseItem);

        return toPurchaseRecordDTO(savedPurchase, savedItems);
    }

    @Override
    @Transactional
    public PurchaseRecordDTO updatePurchase(Long purchaseId, PurchaseRecordDTO purchaseRecordDTO) {
        PurchaseRecordEntity existing = purchaseRecordRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + purchaseId));

        validatePurchaseRequest(purchaseRecordDTO);

        PurchaseRecordEntity updatedPurchase = toPurchaseRecordEntity(purchaseRecordDTO);
        updatedPurchase.setPurchaseId(existing.getPurchaseId());
        updatedPurchase.setPurchaseNo(existing.getPurchaseNo());
        updatedPurchase.setCreatedAt(existing.getCreatedAt());
        updatedPurchase.setUpdatedAt(LocalDateTime.now());
        updatedPurchase.setIsDeleted(existing.getIsDeleted());
        updatedPurchase.setIsActive(existing.getIsActive());

        PurchaseRecordEntity savedPurchase = purchaseRecordRepository.save(updatedPurchase);

        softDeletePurchaseItems(purchaseId);
        List<PurchaseItemEntity> savedItems = savePurchaseItems(savedPurchase, purchaseRecordDTO.getItems());
        savedItems.forEach(this::updateOpeningStockForPurchaseItem);

        return toPurchaseRecordDTO(savedPurchase, savedItems);
    }

    @Override
    public PurchaseRecordDTO getPurchaseById(Long purchaseId) {
        PurchaseRecordEntity purchaseEntity = purchaseRecordRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + purchaseId));
        List<PurchaseItemEntity> items = purchaseItemRepository.findByPurchasePurchaseIdAndIsDeletedFalse(purchaseId);
        return toPurchaseRecordDTO(purchaseEntity, items);
    }

    @Override
    public PurchaseRecordDTO getPurchaseByNumber(String purchaseNo) {
        if (purchaseNo == null || purchaseNo.trim().isEmpty()) {
            throw new IllegalArgumentException("purchaseNo is required");
        }
        PurchaseRecordEntity purchaseEntity = purchaseRecordRepository.findByPurchaseNoAndIsDeletedFalse(purchaseNo.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with purchaseNo: " + purchaseNo));
        List<PurchaseItemEntity> items = purchaseItemRepository.findByPurchasePurchaseIdAndIsDeletedFalse(purchaseEntity.getPurchaseId());
        return toPurchaseRecordDTO(purchaseEntity, items);
    }

    @Override
    @Transactional
    public PurchaseReturnDTO createPurchaseReturn(String purchaseNo, PurchaseReturnRequestDTO returnRequest) {
        if (purchaseNo == null || purchaseNo.trim().isEmpty()) {
            throw new IllegalArgumentException("purchaseNo is required");
        }
        if (returnRequest == null || returnRequest.getItems() == null || returnRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("Return request must contain at least one item");
        }

        PurchaseRecordEntity purchaseEntity = purchaseRecordRepository.findByPurchaseNoAndIsDeletedFalse(purchaseNo.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with purchaseNo: " + purchaseNo));

        Long purchaseId = purchaseEntity.getPurchaseId();
        List<PurchaseItemEntity> purchaseItems = purchaseItemRepository.findByPurchasePurchaseIdAndIsDeletedFalse(purchaseId);
        if (purchaseItems.isEmpty()) {
            throw new IllegalStateException("Purchase has no items available for return");
        }

        Map<Long, PurchaseItemEntity> purchaseItemMap = purchaseItems.stream()
                .collect(Collectors.toMap(PurchaseItemEntity::getPurchaseItemId, item -> item));

        PurchaseReturnEntity returnEntity = toPurchaseReturnEntity(purchaseEntity, returnRequest);
        PurchaseReturnEntity savedReturn = purchaseReturnRepository.save(returnEntity);

        List<PurchaseReturnItemEntity> savedReturnItems = returnRequest.getItems().stream()
                .map(requestItem -> {
                    PurchaseItemEntity original = purchaseItemMap.get(requestItem.getPurchaseItemId());
                    if (original == null) {
                        throw new ResourceNotFoundException("Purchase item not found with id: " + requestItem.getPurchaseItemId());
                    }
                    validateReturnQuantity(original, requestItem.getQuantity());
                    return toPurchaseReturnItemEntity(savedReturn, original, requestItem);
                })
                .collect(Collectors.toList());

        purchaseReturnItemRepository.saveAll(savedReturnItems);
        return toPurchaseReturnDTO(savedReturn);
    }

    @Override
    public List<PurchaseReturnDTO> getPurchaseReturnsByPurchaseNumber(String purchaseNo) {
        if (purchaseNo == null || purchaseNo.trim().isEmpty()) {
            return Collections.emptyList();
        }
        PurchaseRecordEntity purchaseEntity = purchaseRecordRepository.findByPurchaseNoAndIsDeletedFalse(purchaseNo.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with purchaseNo: " + purchaseNo));
        return purchaseReturnRepository.findByPurchasePurchaseIdAndIsDeletedFalse(purchaseEntity.getPurchaseId())
                .stream()
                .map(this::toPurchaseReturnDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PagedResponse<PurchaseReturnDTO> getPurchaseReturnList(LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        List<PurchaseReturnEntity> returns = purchaseReturnRepository.findAll();
        List<PurchaseReturnDTO> content = returns.stream()
                .filter(entity -> !Boolean.TRUE.equals(entity.getIsDeleted()))
                .map(this::toPurchaseReturnDTO)
                .collect(Collectors.toList());
        return new PagedResponse<>(content, 0, content.size(), content.size(), 1, true, true, content.size(), content.isEmpty());
    }

    @Override
    public List<PurchaseRecordDTO> getAllPurchases() {
        return purchaseRecordRepository.findByIsDeletedFalse().stream()
                .map(record -> {
                    List<PurchaseItemEntity> items = purchaseItemRepository.findByPurchasePurchaseIdAndIsDeletedFalse(record.getPurchaseId());
                    return toPurchaseRecordDTO(record, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseRecordDTO> searchPurchasesByNumber(String purchaseNoPrefix) {
        if (purchaseNoPrefix == null || purchaseNoPrefix.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return purchaseRecordRepository.findByPurchaseNoStartingWithIgnoreCaseAndIsDeletedFalse(purchaseNoPrefix.trim())
                .stream()
                .map(record -> {
                    List<PurchaseItemEntity> items = purchaseItemRepository.findByPurchasePurchaseIdAndIsDeletedFalse(record.getPurchaseId());
                    return toPurchaseRecordDTO(record, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deletePurchase(Long purchaseId) {
        PurchaseRecordEntity purchaseEntity = purchaseRecordRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + purchaseId));
        purchaseEntity.setIsDeleted(true);
        purchaseEntity.setDeletedAt(LocalDateTime.now());
        purchaseRecordRepository.save(purchaseEntity);
        softDeletePurchaseItems(purchaseId);
    }

    private void validatePurchaseRequest(PurchaseRecordDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Purchase request cannot be null");
        }
        if (dto.getSupplierId() == null) {
            throw new IllegalArgumentException("Supplier ID is required");
        }
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Purchase must contain at least one item");
        }
        long validItemsCount = dto.getItems().stream()
                .filter(item -> item.getItemId() != null && item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                .count();
        if (validItemsCount == 0) {
            throw new IllegalArgumentException("Purchase must contain at least one valid item with itemId and positive quantity");
        }
    }

    private PurchaseRecordEntity toPurchaseRecordEntity(PurchaseRecordDTO dto) {
        PurchaseRecordEntity entity = new PurchaseRecordEntity();
        BeanUtils.copyProperties(dto, entity);

        if (dto.getSupplierId() != null) {
            SupplierMasterEntity supplier = supplierMasterRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + dto.getSupplierId()));
            entity.setSupplier(supplier);
        }
        if (dto.getUnitId() != null) {
            UnitMasterEntity unit = unitMasterRepository.findById(dto.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + dto.getUnitId()));
            entity.setUnit(unit);
        }
        if (entity.getPurchaseDate() == null) {
            entity.setPurchaseDate(LocalDate.now());
        }
        if (entity.getFinalAmount() == null) {
            entity.setFinalAmount(calculateFinalAmount(dto));
        }
        return entity;
    }

    private BigDecimal calculateFinalAmount(PurchaseRecordDTO dto) {
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            return dto.getItems().stream()
                    .map(item -> item.getLineTotal() == null ? BigDecimal.ZERO : item.getLineTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    private PurchaseItemEntity toPurchaseItemEntity(PurchaseRecordEntity purchase, PurchaseItemDTO dto) {
        PurchaseItemEntity entity = new PurchaseItemEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setPurchase(purchase);

        if (dto.getItemId() != null) {
            ItemMasterEntity item = itemMasterRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + dto.getItemId()));
            entity.setItem(item);
            if (entity.getBatchCode() == null || entity.getBatchCode().isBlank()) {
                itemOpeningStockRepository.findByItemItemIdAndIsDeletedFalse(dto.getItemId())
                        .map(openingStock -> openingStock.getBatchCode())
                        .filter(batchCode -> batchCode != null && !batchCode.isBlank())
                        .ifPresent(entity::setBatchCode);
            }
        }
        return entity;
    }

    private List<PurchaseItemEntity> savePurchaseItems(PurchaseRecordEntity purchase, List<PurchaseItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        List<PurchaseItemEntity> entities = items.stream()
                .filter(item -> item.getItemId() != null && item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                .map(item -> toPurchaseItemEntity(purchase, item))
                .collect(Collectors.toList());
        return purchaseItemRepository.saveAll(entities);
    }

    private void updateOpeningStockForPurchaseItem(PurchaseItemEntity item) {
        if (item == null || item.getItem() == null) {
            return;
        }
        itemOpeningStockRepository.findByItemItemIdAndBatchCodeAndIsDeletedFalse(item.getItem().getItemId(), item.getBatchCode())
                .ifPresentOrElse(existing -> {
                    Integer existingQuantity = existing.getOpeningStock() == null ? 0 : existing.getOpeningStock();
                    existing.setOpeningStock(existingQuantity + (item.getQuantity() == null ? 0 : item.getQuantity().intValue()));
                    if (item.getRate() != null) {
                        existing.setPurchasePrice(item.getRate());
                    }
                    itemOpeningStockRepository.save(existing);
                }, () -> {
                    com.gst.masterdata.entity.ItemOpeningStockEntity openingStockEntity = new com.gst.masterdata.entity.ItemOpeningStockEntity();
                    openingStockEntity.setItem(item.getItem());
                    openingStockEntity.setBatchCode(item.getBatchCode());
                    openingStockEntity.setOpeningStock(item.getQuantity() == null ? 0 : item.getQuantity().intValue());
                    openingStockEntity.setPurchasePrice(item.getRate());
                    openingStockEntity.setSalePrice(null);
                    openingStockEntity.setMrp(null);
                    openingStockEntity.setIsActive(true);
                    openingStockEntity.setIsDeleted(false);
                    itemOpeningStockRepository.save(openingStockEntity);
                });
    }

    private void validateReturnQuantity(PurchaseItemEntity original, BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Return quantity must be greater than zero");
        }
        if (original.getQuantity() == null) {
            throw new IllegalArgumentException("Original purchase item quantity is missing");
        }
        if (quantity.compareTo(original.getQuantity()) > 0) {
            throw new IllegalArgumentException("Return quantity for item " + original.getPurchaseItemId() + " exceeds purchased quantity");
        }
    }

    private PurchaseReturnEntity toPurchaseReturnEntity(PurchaseRecordEntity purchase, PurchaseReturnRequestDTO dto) {
        PurchaseReturnEntity entity = new PurchaseReturnEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setPurchase(purchase);
        if (entity.getReturnDate() == null) {
            entity.setReturnDate(LocalDate.now());
        }
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal taxableAmount = BigDecimal.ZERO;
        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;
        BigDecimal totalIgst = BigDecimal.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;

        if (dto.getItems() != null) {
            for (PurchaseReturnItemRequestDTO item : dto.getItems()) {
                totalGross = totalGross.add(safe(item.getGrossAmount()));
                totalDiscount = totalDiscount.add(safe(item.getDiscountAmt()));
                taxableAmount = taxableAmount.add(safe(item.getTaxableAmount()));
                totalCgst = totalCgst.add(safe(item.getCgstAmt()));
                totalSgst = totalSgst.add(safe(item.getSgstAmt()));
                totalIgst = totalIgst.add(safe(item.getIgstAmt()));
                finalAmount = finalAmount.add(safe(item.getLineTotal()));
            }
        }

        entity.setTotalGrossAmount(totalGross);
        entity.setTotalDiscount(totalDiscount);
        entity.setTaxableAmount(taxableAmount);
        entity.setTotalCgst(totalCgst);
        entity.setTotalSgst(totalSgst);
        entity.setTotalIgst(totalIgst);
        entity.setFinalAmount(finalAmount);

        return entity;
    }

    private PurchaseReturnItemEntity toPurchaseReturnItemEntity(PurchaseReturnEntity purchaseReturn, PurchaseItemEntity original, PurchaseReturnItemRequestDTO dto) {
        PurchaseReturnItemEntity entity = new PurchaseReturnItemEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setPurchaseReturn(purchaseReturn);
        entity.setPurchaseItem(original);
        if (dto.getItemId() != null) {
            ItemMasterEntity item = itemMasterRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + dto.getItemId()));
            entity.setItem(item);
        } else {
            entity.setItem(original.getItem());
        }
        if (entity.getBatchCode() == null || entity.getBatchCode().isBlank()) {
            entity.setBatchCode(original.getBatchCode());
        }
        if (entity.getHsnCode() == null || entity.getHsnCode().isBlank()) {
            entity.setHsnCode(original.getHsnCode());
        }
        if (entity.getQuantity() == null) {
            entity.setQuantity(BigDecimal.ZERO);
        }
        if (entity.getRate() == null) {
            entity.setRate(original.getRate());
        }
        if (entity.getGrossAmount() == null) {
            entity.setGrossAmount(safe(entity.getRate()).multiply(safe(entity.getQuantity())));
        }
        if (entity.getTaxableAmount() == null) {
            entity.setTaxableAmount(entity.getGrossAmount());
        }
        if (entity.getGstRate() == null) {
            entity.setGstRate(original.getGstRate());
        }
        if (entity.getCgstAmount() == null) {
            entity.setCgstAmount(BigDecimal.ZERO);
        }
        if (entity.getSgstAmount() == null) {
            entity.setSgstAmount(BigDecimal.ZERO);
        }
        if (entity.getIgstAmount() == null) {
            entity.setIgstAmount(BigDecimal.ZERO);
        }
        if (entity.getLineTotal() == null) {
            entity.setLineTotal(safe(entity.getGrossAmount())
                    .subtract(safe(entity.getDiscountAmount()))
                    .add(safe(entity.getCgstAmount()))
                    .add(safe(entity.getSgstAmount()))
                    .add(safe(entity.getIgstAmount())));
        }
        return entity;
    }

    private PurchaseRecordDTO toPurchaseRecordDTO(PurchaseRecordEntity entity, List<PurchaseItemEntity> items) {
        PurchaseRecordDTO dto = new PurchaseRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setItems(items == null ? Collections.emptyList() : items.stream().map(this::toPurchaseItemDTO).collect(Collectors.toList()));
        if (entity.getSupplier() != null) {
            dto.setSupplierId(entity.getSupplier().getSupplierId());
        }
        if (entity.getUnit() != null) {
            dto.setUnitId(entity.getUnit().getUnitId());
        }
        return dto;
    }

    private PurchaseItemDTO toPurchaseItemDTO(PurchaseItemEntity entity) {
        PurchaseItemDTO dto = new PurchaseItemDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getItem() != null) {
            dto.setItemId(entity.getItem().getItemId());
            dto.setItemName(entity.getItem().getItemName());
            dto.setItemCode(entity.getItem().getItemCode());
            dto.setItemUnit(entity.getItem().getUnit());
        }
        return dto;
    }

    private PurchaseReturnDTO toPurchaseReturnDTO(PurchaseReturnEntity entity) {
        PurchaseReturnDTO dto = new PurchaseReturnDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getPurchase() != null) {
            dto.setPurchaseId(entity.getPurchase().getPurchaseId());
            dto.setPurchaseNo(entity.getPurchase().getPurchaseNo());
        }
        List<PurchaseReturnItemEntity> returnItems = purchaseReturnItemRepository.findByPurchaseReturnReturnIdAndIsDeletedFalse(entity.getReturnId());
        dto.setItems(returnItems.stream().map(this::toPurchaseReturnItemDTO).collect(Collectors.toList()));
        return dto;
    }

    private PurchaseReturnItemDTO toPurchaseReturnItemDTO(PurchaseReturnItemEntity entity) {
        PurchaseReturnItemDTO dto = new PurchaseReturnItemDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getItem() != null) {
            dto.setItemId(entity.getItem().getItemId());
            dto.setItemName(entity.getItem().getItemName());
            dto.setItemCode(entity.getItem().getItemCode());
            dto.setItemUnit(entity.getItem().getUnit());
        }
        if (entity.getPurchaseItem() != null) {
            dto.setPurchaseItemId(entity.getPurchaseItem().getPurchaseItemId());
        }
        return dto;
    }

    private String generateNextPurchaseNo(LocalDate date) {
        String fy = computeFinancialYear(date);
        int attempts = 0;
        while (attempts < MAX_SEQUENCE_RETRIES) {
            attempts++;
            var opt = purchaseSequenceRepository.findByFy(fy);
            PurchaseSequenceEntity seq;
            if (opt.isPresent()) {
                seq = opt.get();
                seq.setLastNumber(seq.getLastNumber() + 1);
            } else {
                seq = new PurchaseSequenceEntity();
                seq.setFy(fy);
                seq.setLastNumber(1);
            }
            seq = purchaseSequenceRepository.save(seq);
            return String.format("PUR/%s/%04d", fy, seq.getLastNumber());
        }
        throw new RuntimeException("Failed to generate purchase number after retries");
    }

    private String computeFinancialYear(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        int year = date.getYear();
        LocalDate aprFirst = LocalDate.of(year, 4, 1);
        int start, end;
        if (date.isBefore(aprFirst)) {
            start = year - 1;
            end = year;
        } else {
            start = year;
            end = year + 1;
        }
        return String.format("%02d-%02d", start % 100, end % 100);
    }

    private void softDeletePurchaseItems(Long purchaseId) {
        purchaseItemRepository.findByPurchasePurchaseIdAndIsDeletedFalse(purchaseId).forEach(item -> {
            item.setIsDeleted(true);
            item.setDeletedAt(LocalDateTime.now());
        });
        purchaseItemRepository.flush();
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
