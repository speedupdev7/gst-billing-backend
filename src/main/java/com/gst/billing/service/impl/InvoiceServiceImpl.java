package com.gst.billing.service.impl;

import com.gst.billing.dto.InvoiceBalanceDTO;
import com.gst.billing.dto.InvoiceBalanceDetailDTO;
import com.gst.billing.dto.InvoiceItemDTO;
import com.gst.billing.dto.InvoicePaymentDTO;
import com.gst.billing.dto.InvoiceRecordDTO;
import com.gst.billing.dto.InvoiceReturnDTO;
import com.gst.billing.dto.InvoiceReturnItemDTO;
import com.gst.billing.dto.InvoiceReturnItemRequestDTO;
import com.gst.billing.dto.InvoiceReturnListDTO;
import com.gst.billing.dto.GstSlabDTO;
import com.gst.billing.dto.InvoiceReturnRequestDTO;
import com.gst.billing.dto.PagedResponse;
import com.gst.billing.entity.InvoiceBalanceEntity;
import com.gst.billing.entity.InvoiceItemEntity;
import com.gst.billing.entity.InvoicePaymentEntity;
import com.gst.billing.entity.InvoiceRecordEntity;
import com.gst.billing.entity.InvoiceReturnEntity;
import com.gst.billing.entity.InvoiceReturnItemEntity;
import com.gst.billing.repository.InvoiceBalanceRepository;
import com.gst.billing.repository.InvoiceItemRepository;
import com.gst.billing.repository.InvoicePaymentRepository;
import com.gst.billing.repository.InvoiceRecordRepository;
import com.gst.billing.repository.InvoiceReturnItemRepository;
import com.gst.billing.repository.InvoiceReturnRepository;
import com.gst.billing.service.InvoiceService;
import com.gst.masterdata.entity.CustomerMasterEntity;
import com.gst.masterdata.entity.ItemMasterEntity;
import com.gst.masterdata.entity.UnitMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.CustomerMasterRepository;
import com.gst.masterdata.repository.ItemMasterRepository;
import com.gst.masterdata.repository.ItemOpeningStockRepository;
import com.gst.masterdata.repository.UnitMasterRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);


    @Autowired
    private InvoiceRecordRepository invoiceRecordRepository;

    @Autowired
    private com.gst.billing.repository.InvoiceSequenceRepository invoiceSequenceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private InvoicePaymentRepository invoicePaymentRepository;

    @Autowired
    private InvoiceBalanceRepository invoiceBalanceRepository;

    private static final int MAX_SEQUENCE_RETRIES = 8;


    @Autowired
    private InvoiceReturnRepository invoiceReturnRepository;

    @Autowired
    private InvoiceReturnItemRepository invoiceReturnItemRepository;

    @Autowired
    private CustomerMasterRepository customerMasterRepository;

    @Autowired
    private ItemMasterRepository itemMasterRepository;

    @Autowired
    private ItemOpeningStockRepository itemOpeningStockRepository;

    @Autowired
    private UnitMasterRepository unitMasterRepository;

    @Override
    @Transactional
    public InvoiceRecordDTO createInvoice(InvoiceRecordDTO invoiceRecordDTO) {
        log.debug("createInvoice called with customerId={} invoiceDate={}", invoiceRecordDTO == null ? null : invoiceRecordDTO.getCustomerId(),
            invoiceRecordDTO == null ? null : invoiceRecordDTO.getInvoiceDate());
        validateInvoiceRequest(invoiceRecordDTO);
        preparePaymentsAndBalance(invoiceRecordDTO);
        InvoiceRecordEntity invoiceRecordEntity = toInvoiceRecordEntity(invoiceRecordDTO);
        // always generate invoice number server-side - never use client input
        LocalDate dt = invoiceRecordEntity.getInvoiceDate() != null ? invoiceRecordEntity.getInvoiceDate() : LocalDate.now();
        String invoiceNo = generateNextInvoiceNo(dt);
        invoiceRecordEntity.setInvoiceNo(invoiceNo);
        InvoiceRecordEntity savedInvoice = invoiceRecordRepository.save(invoiceRecordEntity);
        log.debug("Saved invoice id={} customerId={}", savedInvoice.getInvoiceId(),
            savedInvoice.getCustomer() == null ? null : savedInvoice.getCustomer().getCustomerId());

        List<InvoiceItemEntity> savedItems = saveInvoiceItems(savedInvoice, invoiceRecordDTO.getItems());
        InvoiceBalanceEntity savedBalance = saveInvoiceBalance(savedInvoice, invoiceRecordDTO.getBalance());
        List<InvoicePaymentEntity> savedPayments = saveInvoicePayments(savedInvoice, invoiceRecordDTO.getPayments());

        return toInvoiceRecordDTO(savedInvoice, savedItems, savedBalance, savedPayments);
    }

    @Override
    @Transactional
    public byte[] saveAndPrintInvoice(InvoiceRecordDTO invoiceRecordDTO) {
        InvoiceRecordDTO savedInvoice = createInvoice(invoiceRecordDTO);
        try {
            return buildInvoicePdf(savedInvoice);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to generate invoice PDF", ex);
        }
    }

    @Override
    @Transactional
    public InvoiceRecordDTO updateInvoice(Long invoiceId, InvoiceRecordDTO invoiceRecordDTO) {
        InvoiceRecordEntity existingInvoice = invoiceRecordRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        validateInvoiceRequest(invoiceRecordDTO);
        preparePaymentsAndBalance(invoiceRecordDTO);

        InvoiceRecordEntity updatedInvoice = toInvoiceRecordEntity(invoiceRecordDTO);
        updatedInvoice.setInvoiceId(existingInvoice.getInvoiceId());
        updatedInvoice.setCreatedAt(existingInvoice.getCreatedAt());
        updatedInvoice.setUpdatedAt(LocalDateTime.now());
        updatedInvoice.setIsDeleted(existingInvoice.getIsDeleted());
        updatedInvoice.setIsActive(existingInvoice.getIsActive());

        InvoiceRecordEntity savedInvoice = invoiceRecordRepository.save(updatedInvoice);

        softDeleteInvoiceItems(invoiceId);
        softDeleteInvoicePayments(invoiceId);
        softDeleteInvoiceBalance(invoiceId);

        List<InvoiceItemEntity> savedItems = saveInvoiceItems(savedInvoice, invoiceRecordDTO.getItems());
        InvoiceBalanceEntity savedBalance = saveInvoiceBalance(savedInvoice, invoiceRecordDTO.getBalance());
        List<InvoicePaymentEntity> savedPayments = saveInvoicePayments(savedInvoice, invoiceRecordDTO.getPayments());

        return toInvoiceRecordDTO(savedInvoice, savedItems, savedBalance, savedPayments);
    }

    @Override
    public InvoiceRecordDTO getInvoiceById(Long invoiceId) {
        InvoiceRecordEntity invoiceEntity = invoiceRecordRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId);
        InvoiceBalanceEntity balance = invoiceBalanceRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId).orElse(null);
        List<InvoicePaymentEntity> payments = invoicePaymentRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId);

        return toInvoiceRecordDTO(invoiceEntity, items, balance, payments);
    }

    @Override
    public InvoiceRecordDTO getInvoiceByNumber(String invoiceNo) {
        if (invoiceNo == null || invoiceNo.trim().isEmpty()) {
            throw new IllegalArgumentException("invoiceNo is required");
        }
        InvoiceRecordEntity invoiceEntity = invoiceRecordRepository.findByInvoiceNoAndIsDeletedFalse(invoiceNo.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with invoiceNo: " + invoiceNo));

        Long invoiceId = invoiceEntity.getInvoiceId();
        List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId);
        InvoiceBalanceEntity balance = invoiceBalanceRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId).orElse(null);
        List<InvoicePaymentEntity> payments = invoicePaymentRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId);

        return toInvoiceRecordDTO(invoiceEntity, items, balance, payments);
    }

    @Override
    @Transactional
    public InvoiceReturnDTO createInvoiceReturn(String invoiceNo, InvoiceReturnRequestDTO returnRequest) {
        if (invoiceNo == null || invoiceNo.trim().isEmpty()) {
            throw new IllegalArgumentException("invoiceNo is required");
        }
        if (returnRequest == null || returnRequest.getItems() == null || returnRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("Return request must contain at least one item");
        }

        InvoiceRecordEntity invoiceEntity = invoiceRecordRepository.findByInvoiceNoAndIsDeletedFalse(invoiceNo.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with invoiceNo: " + invoiceNo));

        Long invoiceId = invoiceEntity.getInvoiceId();
        List<InvoiceItemEntity> invoiceItems = invoiceItemRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId);
        if (invoiceItems.isEmpty()) {
            throw new IllegalStateException("Invoice has no items available for return");
        }

        var invoiceItemMap = invoiceItems.stream()
                .collect(java.util.stream.Collectors.toMap(InvoiceItemEntity::getInvoiceItemId, item -> item));

        var existingReturnQuantities = invoiceReturnItemRepository.findByInvoiceReturnInvoiceInvoiceIdAndIsDeletedFalse(invoiceId)
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(item -> item.getInvoiceItem().getInvoiceItemId(),
                        java.util.stream.Collectors.reducing(BigDecimal.ZERO, item -> safe(item.getQuantity()), BigDecimal::add)));

        InvoiceReturnEntity returnEntity = toInvoiceReturnEntity(invoiceEntity, returnRequest);
        InvoiceReturnEntity savedReturn = invoiceReturnRepository.save(returnEntity);

        List<InvoiceReturnItemEntity> savedReturnItems = returnRequest.getItems().stream()
                .map(requestItem -> {
                    InvoiceItemEntity original = invoiceItemMap.get(requestItem.getInvoiceItemId());
                    if (original == null) {
                        throw new ResourceNotFoundException("Invoice item not found with id: " + requestItem.getInvoiceItemId());
                    }

                    BigDecimal returnedQuantity = existingReturnQuantities.getOrDefault(original.getInvoiceItemId(), BigDecimal.ZERO);
                    BigDecimal requestQuantity = safe(requestItem.getQuantity());
                    BigDecimal availableQuantity = safe(original.getQuantity()).subtract(returnedQuantity);
                    if (requestQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Return quantity must be greater than zero");
                    }
                    if (requestQuantity.compareTo(availableQuantity) > 0) {
                        throw new IllegalArgumentException("Return quantity for item " + original.getInvoiceItemId() + " exceeds available quantity");
                    }
                    return toInvoiceReturnItemEntity(savedReturn, original, requestItem);
                })
                .collect(java.util.stream.Collectors.toList());

        savedReturnItems = invoiceReturnItemRepository.saveAll(savedReturnItems);
        return toInvoiceReturnDTO(savedReturn);
    }

    @Override
    public List<InvoiceReturnDTO> getInvoiceReturnsByInvoiceNumber(String invoiceNo) {
        if (invoiceNo == null || invoiceNo.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        InvoiceRecordEntity invoiceEntity = invoiceRecordRepository.findByInvoiceNoAndIsDeletedFalse(invoiceNo.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with invoiceNo: " + invoiceNo));
        return invoiceReturnRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceEntity.getInvoiceId())
                .stream()
                .map(this::toInvoiceReturnDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InvoiceReturnListDTO> getInvoiceReturnList(LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Page<InvoiceReturnEntity> page = invoiceReturnRepository.findByReturnDateRangePageable(fromDate, toDate, pageable);
        List<InvoiceReturnListDTO> content = page.stream()
                .map(this::toInvoiceReturnListDTO)
                .collect(Collectors.toList());
        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst(),
                page.getNumberOfElements(),
                page.isEmpty()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<InvoiceReturnListDTO> getInvoiceReturnListAll(LocalDate fromDate, LocalDate toDate) {
        java.util.List<InvoiceReturnEntity> list = invoiceReturnRepository.findByReturnDateRange(fromDate, toDate);
        return list.stream().map(this::toInvoiceReturnListDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public com.gst.billing.dto.InvoiceReturnTotalsDTO getInvoiceReturnTotals(LocalDate fromDate, LocalDate toDate, String returnType, String reasonCode) {
        java.util.List<InvoiceReturnEntity> list = invoiceReturnRepository.findByReturnDateRange(fromDate, toDate);
        java.math.BigDecimal totalBase = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalTax = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalRefund = java.math.BigDecimal.ZERO;

        for (InvoiceReturnEntity e : list) {
            if (returnType != null && !returnType.trim().isEmpty()) {
                if (e.getReturnType() == null || !e.getReturnType().toLowerCase().contains(returnType.trim().toLowerCase())) continue;
            }
            if (reasonCode != null && !reasonCode.trim().isEmpty()) {
                if (e.getReasonCode() == null || !e.getReasonCode().toLowerCase().contains(reasonCode.trim().toLowerCase())) continue;
            }

            if (e.getTaxableAmount() != null) totalBase = totalBase.add(e.getTaxableAmount());
            if (e.getTotalCgst() != null) totalTax = totalTax.add(e.getTotalCgst());
            if (e.getTotalSgst() != null) totalTax = totalTax.add(e.getTotalSgst());
            if (e.getTotalIgst() != null) totalTax = totalTax.add(e.getTotalIgst());
            if (e.getFinalAmount() != null) totalRefund = totalRefund.add(e.getFinalAmount());
        }

        com.gst.billing.dto.InvoiceReturnTotalsDTO dto = new com.gst.billing.dto.InvoiceReturnTotalsDTO();
        dto.setTotalBase(totalBase);
        dto.setTotalTax(totalTax);
        dto.setTotalRefund(totalRefund);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public com.gst.billing.dto.GstReportDTO getGstReport(LocalDate fromDate, LocalDate toDate, String supplyType, String gstSlab) {
        java.util.List<InvoiceRecordDTO> invoices = getAllInvoices();

        if (fromDate != null && toDate != null) {
            invoices = invoices.stream()
                    .filter(i -> {
                        java.time.LocalDate d = i.getInvoiceDate();
                        return d != null && (!d.isBefore(fromDate)) && (!d.isAfter(toDate));
                    })
                    .collect(Collectors.toList());
        }

        // supplyType filter: if 'Interstate' then igst>0, if 'Intrastate' then cgst+sgst>0
        if (supplyType != null && !supplyType.trim().isEmpty() && !"All".equalsIgnoreCase(supplyType.trim())) {
            String st = supplyType.trim().toLowerCase();
            if (st.contains("inter")) {
                invoices = invoices.stream().filter(i -> i.getTotalIgst() != null && i.getTotalIgst().compareTo(java.math.BigDecimal.ZERO) > 0).collect(Collectors.toList());
            } else if (st.contains("intra")) {
                invoices = invoices.stream().filter(i -> ((i.getTotalCgst() != null && i.getTotalCgst().compareTo(java.math.BigDecimal.ZERO) > 0) || (i.getTotalSgst() != null && i.getTotalSgst().compareTo(java.math.BigDecimal.ZERO) > 0))).collect(Collectors.toList());
            }
        }

        java.math.BigDecimal taxableTurnover = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalGstLiability = java.math.BigDecimal.ZERO;
        java.math.BigDecimal cgstCollected = java.math.BigDecimal.ZERO;
        java.math.BigDecimal sgstCollected = java.math.BigDecimal.ZERO;
        java.math.BigDecimal igstCollected = java.math.BigDecimal.ZERO;

        // slab aggregation by gstRate string
        java.util.Map<String, java.util.Set<String>> slabInvoiceSet = new java.util.HashMap<>(); // rate -> set of invoiceNos
        java.util.Map<String, java.math.BigDecimal> slabTaxable = new java.util.HashMap<>();
        java.util.Map<String, java.math.BigDecimal> slabGst = new java.util.HashMap<>();

        for (InvoiceRecordDTO inv : invoices) {
            if (inv.getTaxableAmount() != null) taxableTurnover = taxableTurnover.add(inv.getTaxableAmount());
            java.math.BigDecimal gst = java.math.BigDecimal.ZERO;
            if (inv.getTotalCgst() != null) { cgstCollected = cgstCollected.add(inv.getTotalCgst()); gst = gst.add(inv.getTotalCgst()); }
            if (inv.getTotalSgst() != null) { sgstCollected = sgstCollected.add(inv.getTotalSgst()); gst = gst.add(inv.getTotalSgst()); }
            if (inv.getTotalIgst() != null) { igstCollected = igstCollected.add(inv.getTotalIgst()); gst = gst.add(inv.getTotalIgst()); }
            totalGstLiability = totalGstLiability.add(gst);

            // per-item slab aggregation
            if (inv.getItems() != null) {
                for (InvoiceItemDTO item : inv.getItems()) {
                    String rateKey = item.getGstRate() != null ? item.getGstRate().toPlainString() : "0";

                    // apply gstSlab filter if provided
                    if (gstSlab != null && !gstSlab.trim().isEmpty() && !"All".equalsIgnoreCase(gstSlab.trim())) {
                        try {
                            java.math.BigDecimal slabFilter = new java.math.BigDecimal(gstSlab.trim());
                            if (item.getGstRate() == null || item.getGstRate().compareTo(slabFilter) != 0) {
                                continue;
                            }
                        } catch (Exception ex) {
                            // ignore parsing error and include all
                        }
                    }

                    slabInvoiceSet.computeIfAbsent(rateKey, k -> new java.util.HashSet<>()).add(inv.getInvoiceNo());
                    slabTaxable.put(rateKey, slabTaxable.getOrDefault(rateKey, java.math.BigDecimal.ZERO).add(item.getTaxableAmount() == null ? java.math.BigDecimal.ZERO : item.getTaxableAmount()));
                    java.math.BigDecimal itemGst = java.math.BigDecimal.ZERO;
                    if (item.getCgstAmt() != null) itemGst = itemGst.add(item.getCgstAmt());
                    if (item.getSgstAmt() != null) itemGst = itemGst.add(item.getSgstAmt());
                    if (item.getIgstAmt() != null) itemGst = itemGst.add(item.getIgstAmt());
                    slabGst.put(rateKey, slabGst.getOrDefault(rateKey, java.math.BigDecimal.ZERO).add(itemGst));
                }
            }
        }

        java.util.List<GstSlabDTO> slabs = new java.util.ArrayList<>();
        for (String rateKey : slabTaxable.keySet()) {
            GstSlabDTO s = new GstSlabDTO();
            s.setGstRate(rateKey + "%");
            s.setInvoices(slabInvoiceSet.getOrDefault(rateKey, java.util.Collections.emptySet()).size());
            s.setTaxableValue(slabTaxable.getOrDefault(rateKey, java.math.BigDecimal.ZERO));
            s.setGstAmount(slabGst.getOrDefault(rateKey, java.math.BigDecimal.ZERO));
            // effective rate = gstAmount / taxableValue *100 if taxableValue>0
            java.math.BigDecimal effective = java.math.BigDecimal.ZERO;
            if (s.getTaxableValue() != null && s.getTaxableValue().compareTo(java.math.BigDecimal.ZERO) > 0) {
                effective = s.getGstAmount().multiply(java.math.BigDecimal.valueOf(100)).divide(s.getTaxableValue(), 2, java.math.RoundingMode.HALF_UP);
            }
            s.setEffectiveRate(effective);
            slabs.add(s);
        }

        com.gst.billing.dto.GstReportDTO out = new com.gst.billing.dto.GstReportDTO();
        out.setTaxableTurnover(taxableTurnover);
        out.setTotalGstLiability(totalGstLiability);
        out.setFiledCount(0); // no filed flag available; front-end may supply or compute separately
        out.setPendingCount(0);
        out.setCgstCollected(cgstCollected);
        out.setSgstCollected(sgstCollected);
        out.setIgstCollected(igstCollected);
        out.setSlabBreakup(slabs);
        return out;
    }

    @Override
    public List<InvoiceRecordDTO> getAllInvoices() {
        return invoiceRecordRepository.findByIsDeletedFalse().stream()
                .map(record -> {
                    List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(record.getInvoiceId());
                    InvoiceBalanceEntity balance = invoiceBalanceRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(record.getInvoiceId()).orElse(null);
                    List<InvoicePaymentEntity> payments = invoicePaymentRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(record.getInvoiceId());
                    return toInvoiceRecordDTO(record, items, balance, payments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceRecordDTO> searchInvoicesByNumber(String invoiceNoPrefix) {
        if (invoiceNoPrefix == null || invoiceNoPrefix.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return invoiceRecordRepository.findByInvoiceNoStartingWithIgnoreCaseAndIsDeletedFalse(invoiceNoPrefix.trim())
                .stream()
                .map(record -> {
                    List<InvoiceItemEntity> items = invoiceItemRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(record.getInvoiceId());
                    InvoiceBalanceEntity balance = invoiceBalanceRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(record.getInvoiceId()).orElse(null);
                    List<InvoicePaymentEntity> payments = invoicePaymentRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(record.getInvoiceId());
                    return toInvoiceRecordDTO(record, items, balance, payments);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteInvoice(Long invoiceId) {
        InvoiceRecordEntity invoiceEntity = invoiceRecordRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));
        invoiceEntity.setIsDeleted(true);
        invoiceEntity.setDeletedAt(LocalDateTime.now());
        invoiceRecordRepository.save(invoiceEntity);
        softDeleteInvoiceItems(invoiceId);
        softDeleteInvoicePayments(invoiceId);
        softDeleteInvoiceBalance(invoiceId);
    }

    @Override
    public List<InvoiceBalanceDetailDTO> getAllInvoiceBalances() {
        return invoiceBalanceRepository.findAllInvoiceBalances().stream()
                .map(this::toInvoiceBalanceDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBalanceDetailDTO> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return invoiceBalanceRepository.findInvoicesByDateRange(startDate, endDate).stream()
                .map(this::toInvoiceBalanceDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InvoiceBalanceDetailDTO> getAllInvoiceBalancesPageable(Pageable pageable) {
        Pageable normalized = normalizeInvoiceBalanceSort(pageable);
        return toPagedResponse(invoiceBalanceRepository.findAllInvoiceBalancesPageable(normalized));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InvoiceBalanceDetailDTO> getInvoicesByDateRangePageable(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        Pageable normalized = normalizeInvoiceBalanceSort(pageable);
        return toPagedResponse(invoiceBalanceRepository.findInvoicesByDateRangePageable(startDate, endDate, normalized));
    }

    private PagedResponse<InvoiceBalanceDetailDTO> toPagedResponse(Page<InvoiceBalanceEntity> page) {
        return new PagedResponse<>(
                page.map(this::toInvoiceBalanceDetailDTO).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst(),
                page.getNumberOfElements(),
                page.isEmpty()
        );
    }

    private Pageable normalizeInvoiceBalanceSort(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return pageable;
        }
        List<Sort.Order> normalizedOrders = pageable.getSort().stream()
                .map(order -> {
                    String property = order.getProperty();
                    if ("invoiceDate".equals(property)) {
                        return new Sort.Order(order.getDirection(), "invoice.invoiceDate");
                    }
                    return order;
                })
                .toList();
        Sort normalizedSort = Sort.by(normalizedOrders);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), normalizedSort);
    }

    private void validateInvoiceRequest(InvoiceRecordDTO invoiceRecordDTO) {
        if (invoiceRecordDTO == null) {
            throw new IllegalArgumentException("Invoice request cannot be null");
        }
        if (invoiceRecordDTO.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (invoiceRecordDTO.getItems() == null || invoiceRecordDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must contain at least one item");
        }
        long validItemsCount = invoiceRecordDTO.getItems().stream()
                .filter(item -> item.getItemId() != null && item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                .count();
        if (validItemsCount == 0) {
            throw new IllegalArgumentException("Invoice must contain at least one valid item with itemId and positive quantity");
        }
        if (invoiceRecordDTO.getPayments() == null || invoiceRecordDTO.getPayments().isEmpty()) {
            throw new IllegalArgumentException("Payments array cannot be null or empty");
        }
    }

    private InvoiceRecordEntity toInvoiceRecordEntity(InvoiceRecordDTO dto) {
        InvoiceRecordEntity entity = new InvoiceRecordEntity();
        BeanUtils.copyProperties(dto, entity);

        if (dto.getCustomerId() != null) {
            log.debug("Resolving customer for id={}", dto.getCustomerId());
            CustomerMasterEntity customer = customerMasterRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));
            entity.setCustomer(customer);
            log.debug("Resolved customer id={} name={}", customer.getCustomerId(), customer.getCustomerName());
        }

        if (dto.getUnitId() != null) {
            UnitMasterEntity unit = unitMasterRepository.findById(dto.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + dto.getUnitId()));
            entity.setUnit(unit);
        }

        if (dto.getFinalAmount() == null) {
            entity.setFinalAmount(calculateFinalAmount(dto));
        }

        return entity;
    }

    private BigDecimal calculateFinalAmount(InvoiceRecordDTO dto) {
        if (dto.getBalance() != null && dto.getBalance().getInvoiceAmount() != null) {
            return dto.getBalance().getInvoiceAmount();
        }
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            return dto.getItems().stream()
                    .map(item -> item.getLineTotal() == null ? BigDecimal.ZERO : item.getLineTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    private void preparePaymentsAndBalance(InvoiceRecordDTO dto) {
        if (dto == null) {
            return;
        }

        if (dto.getPayments() != null && !dto.getPayments().isEmpty()) {
            dto.getPayments().forEach(payment -> {
                if (payment.getPaymentDate() == null) {
                    payment.setPaymentDate(dto.getInvoiceDate() != null ? dto.getInvoiceDate() : LocalDate.now());
                }
            });
        }

        if (dto.getBalance() == null) {
            dto.setBalance(new InvoiceBalanceDTO());
        }

        BigDecimal invoiceAmount = dto.getFinalAmount() != null ? dto.getFinalAmount() : calculateFinalAmount(dto);
        BigDecimal paidAmount = dto.getPayments() == null ? BigDecimal.ZERO : dto.getPayments().stream()
                .map(payment -> payment.getAmount() == null ? BigDecimal.ZERO : payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balanceAmount = invoiceAmount.subtract(paidAmount);
        String status = paidAmount.compareTo(invoiceAmount) >= 0 ? "Paid" : paidAmount.compareTo(BigDecimal.ZERO) > 0 ? "Partially Paid" : "Unpaid";

        InvoiceBalanceDTO balance = dto.getBalance();
        if (balance.getInvoiceAmount() == null) {
            balance.setInvoiceAmount(invoiceAmount);
        }
        balance.setPaidAmount(paidAmount);
        balance.setBalanceAmount(balanceAmount);
        if (balance.getStatus() == null || balance.getStatus().isBlank()) {
            balance.setStatus(status);
        }
    }

    private InvoiceItemEntity toInvoiceItemEntity(InvoiceRecordEntity invoice, InvoiceItemDTO dto) {
        InvoiceItemEntity entity = new InvoiceItemEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setInvoice(invoice);

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

    private List<InvoiceItemEntity> saveInvoiceItems(InvoiceRecordEntity invoice, List<InvoiceItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        List<InvoiceItemEntity> entities = items.stream()
                .filter(item -> item.getItemId() != null && item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                .map(item -> toInvoiceItemEntity(invoice, item))
                .collect(Collectors.toList());
        return invoiceItemRepository.saveAll(entities);
    }

    private InvoiceBalanceEntity saveInvoiceBalance(InvoiceRecordEntity invoice, InvoiceBalanceDTO balanceDTO) {
        if (balanceDTO == null) {
            return null;
        }
        InvoiceBalanceEntity balanceEntity = new InvoiceBalanceEntity();
        BeanUtils.copyProperties(balanceDTO, balanceEntity);
        balanceEntity.setInvoice(invoice);
        return invoiceBalanceRepository.save(balanceEntity);
    }

    private List<InvoicePaymentEntity> saveInvoicePayments(InvoiceRecordEntity invoice, List<InvoicePaymentDTO> payments) {
        if (payments == null || payments.isEmpty()) {
            return Collections.emptyList();
        }
        List<InvoicePaymentEntity> entities = payments.stream().map(paymentDTO -> {
            InvoicePaymentEntity entity = new InvoicePaymentEntity();
            BeanUtils.copyProperties(paymentDTO, entity);
            entity.setInvoice(invoice);
            if (entity.getPaymentDate() == null) {
                entity.setPaymentDate(invoice.getInvoiceDate() != null ? invoice.getInvoiceDate() : LocalDate.now());
            }
            return entity;
        }).collect(Collectors.toList());
        return invoicePaymentRepository.saveAll(entities);
    }

    private void softDeleteInvoiceItems(Long invoiceId) {
        invoiceItemRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId).forEach(item -> {
            item.setIsDeleted(true);
            item.setDeletedAt(LocalDateTime.now());
        });
        invoiceItemRepository.flush();
    }

    private void softDeleteInvoicePayments(Long invoiceId) {
        invoicePaymentRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId).forEach(payment -> {
            payment.setIsDeleted(true);
            payment.setDeletedAt(LocalDateTime.now());
        });
        invoicePaymentRepository.flush();
    }

    private void softDeleteInvoiceBalance(Long invoiceId) {
        invoiceBalanceRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(invoiceId).ifPresent(balance -> {
            balance.setIsDeleted(true);
            balance.setDeletedAt(LocalDateTime.now());
            invoiceBalanceRepository.save(balance);
        });
    }

    private InvoiceRecordDTO toInvoiceRecordDTO(InvoiceRecordEntity entity,
                                                List<InvoiceItemEntity> items,
                                                InvoiceBalanceEntity balance,
                                                List<InvoicePaymentEntity> payments) {
        InvoiceRecordDTO dto = new InvoiceRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setItems(items == null ? Collections.emptyList() : items.stream().map(this::toInvoiceItemDTO).collect(Collectors.toList()));
        dto.setBalance(balance == null ? null : toInvoiceBalanceDTO(balance));
        dto.setPayments(payments == null ? Collections.emptyList() : payments.stream().map(this::toInvoicePaymentDTO).collect(Collectors.toList()));
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getCustomerId());
            dto.setCustomerName(entity.getCustomer().getCustomerName());
        }
        if (entity.getUnit() != null) {
            dto.setUnitId(entity.getUnit().getUnitId());
            dto.setUnitName(entity.getUnit().getUnitName());
        }

        if (entity.getInvoiceId() != null) {
            List<InvoiceReturnEntity> returns = invoiceReturnRepository.findByInvoiceInvoiceIdAndIsDeletedFalse(entity.getInvoiceId());
            List<InvoiceReturnItemEntity> returnItems = invoiceReturnItemRepository.findByInvoiceReturnInvoiceInvoiceIdAndIsDeletedFalse(entity.getInvoiceId());

            if (!returns.isEmpty()) {
                dto.setReturns(returns.stream().map(this::toInvoiceReturnDTO).collect(Collectors.toList()));
                dto.setTotalReturnAmount(returns.stream()
                        .map(returnEntity -> safe(returnEntity.getFinalAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                dto.setTotalReturnCgst(returns.stream()
                        .map(returnEntity -> safe(returnEntity.getTotalCgst()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                dto.setTotalReturnSgst(returns.stream()
                        .map(returnEntity -> safe(returnEntity.getTotalSgst()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                dto.setTotalReturnIgst(returns.stream()
                        .map(returnEntity -> safe(returnEntity.getTotalIgst()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                dto.setTotalReturnTaxableAmount(returns.stream()
                        .map(returnEntity -> safe(returnEntity.getTaxableAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            } else {
                dto.setReturns(Collections.emptyList());
            }

            var returnedQuantities = returnItems.stream()
                    .collect(Collectors.groupingBy(item -> item.getInvoiceItem().getInvoiceItemId(),
                            Collectors.reducing(BigDecimal.ZERO, item -> safe(item.getQuantity()), BigDecimal::add)));

            if (dto.getItems() != null) {
                dto.getItems().forEach(item -> {
                    BigDecimal returnedQuantity = returnedQuantities.getOrDefault(item.getInvoiceItemId(), BigDecimal.ZERO);
                    item.setReturnedQuantity(returnedQuantity);
                    BigDecimal originalQuantity = safe(item.getQuantity());
                    BigDecimal remainingQuantity = originalQuantity.subtract(returnedQuantity);
                    if (remainingQuantity.compareTo(BigDecimal.ZERO) < 0) {
                        remainingQuantity = BigDecimal.ZERO;
                    }
                    item.setQuantity(remainingQuantity);
                    item.setAvailableQuantity(remainingQuantity);
                    if (originalQuantity.compareTo(BigDecimal.ZERO) > 0) {
                        adjustItemAmountsForReturns(item, remainingQuantity, originalQuantity);
                    }
                });
            }

            adjustInvoiceAmountsForReturns(dto);
        }

        return dto;
    }

    private void adjustInvoiceAmountsForReturns(InvoiceRecordDTO dto) {
        if (dto == null || dto.getReturns() == null || dto.getReturns().isEmpty()) {
            return;
        }

        BigDecimal totalReturnGross = dto.getReturns().stream()
                .map(returnEntity -> safe(returnEntity.getTotalGrossAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalReturnDiscount = dto.getReturns().stream()
                .map(returnEntity -> safe(returnEntity.getTotalDiscount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalReturnTaxable = dto.getReturns().stream()
                .map(returnEntity -> safe(returnEntity.getTaxableAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalReturnCgst = safe(dto.getTotalReturnCgst());
        BigDecimal totalReturnSgst = safe(dto.getTotalReturnSgst());
        BigDecimal totalReturnIgst = safe(dto.getTotalReturnIgst());
        BigDecimal totalReturnFinal = safe(dto.getTotalReturnAmount());

        dto.setTotalGrossAmount(safe(dto.getTotalGrossAmount()).subtract(totalReturnGross));
        dto.setTotalDiscount(safe(dto.getTotalDiscount()).subtract(totalReturnDiscount));
        dto.setTaxableAmount(safe(dto.getTaxableAmount()).subtract(totalReturnTaxable));
        dto.setTotalCgst(safe(dto.getTotalCgst()).subtract(totalReturnCgst));
        dto.setTotalSgst(safe(dto.getTotalSgst()).subtract(totalReturnSgst));
        dto.setTotalIgst(safe(dto.getTotalIgst()).subtract(totalReturnIgst));
        dto.setFinalAmount(safe(dto.getFinalAmount()).subtract(totalReturnFinal));

        if (dto.getBalance() == null) {
            dto.setBalance(new InvoiceBalanceDTO());
        }

        BigDecimal paidAmount = dto.getPayments() == null ? BigDecimal.ZERO : dto.getPayments().stream()
                .map(payment -> payment.getAmount() == null ? BigDecimal.ZERO : payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal invoiceAmount = safe(dto.getFinalAmount());
        BigDecimal balanceAmount = invoiceAmount.subtract(paidAmount);

        InvoiceBalanceDTO balance = dto.getBalance();
        balance.setInvoiceAmount(invoiceAmount);
        balance.setPaidAmount(paidAmount);
        balance.setBalanceAmount(balanceAmount);
        if (balance.getStatus() == null || balance.getStatus().isBlank()) {
            String status = paidAmount.compareTo(invoiceAmount) >= 0 ? "Paid"
                    : paidAmount.compareTo(BigDecimal.ZERO) > 0 ? "Partially Paid" : "Unpaid";
            balance.setStatus(status);
        }
    }

    private void adjustItemAmountsForReturns(InvoiceItemDTO item, BigDecimal remainingQuantity, BigDecimal originalQuantity) {
        if (originalQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal ratio = remainingQuantity.divide(originalQuantity, 10, java.math.RoundingMode.HALF_UP);
        item.setGrossAmount(safe(item.getGrossAmount()).multiply(ratio));
        item.setDiscountAmt(safe(item.getDiscountAmt()).multiply(ratio));
        item.setTaxableAmount(safe(item.getTaxableAmount()).multiply(ratio));
        item.setCgstAmt(safe(item.getCgstAmt()).multiply(ratio));
        item.setSgstAmt(safe(item.getSgstAmt()).multiply(ratio));
        item.setIgstAmt(safe(item.getIgstAmt()).multiply(ratio));
        item.setLineTotal(safe(item.getLineTotal()).multiply(ratio));
    }

    private InvoiceReturnDTO toInvoiceReturnDTO(InvoiceReturnEntity entity) {
        InvoiceReturnDTO dto = new InvoiceReturnDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getInvoice() != null) {
            dto.setInvoiceId(entity.getInvoice().getInvoiceId());
            dto.setInvoiceNo(entity.getInvoice().getInvoiceNo());
        }
        List<InvoiceReturnItemEntity> returnItems = invoiceReturnItemRepository.findByInvoiceReturnReturnIdAndIsDeletedFalse(entity.getReturnId());
        dto.setItems(returnItems.stream().map(this::toInvoiceReturnItemDTO).collect(Collectors.toList()));
        return dto;
    }

    private InvoiceReturnListDTO toInvoiceReturnListDTO(InvoiceReturnEntity entity) {
        InvoiceReturnListDTO dto = new InvoiceReturnListDTO();
        dto.setReturnNo(entity.getReturnNo());
        dto.setInvoiceNo(entity.getInvoice() != null ? entity.getInvoice().getInvoiceNo() : null);
        dto.setReturnDate(entity.getReturnDate());
        if (entity.getInvoice() != null && entity.getInvoice().getCustomer() != null) {
            dto.setCustomerName(entity.getInvoice().getCustomer().getCustomerName());
        }
        dto.setReasonCode(entity.getReasonCode());
        dto.setFinalAmount(entity.getFinalAmount());
        return dto;
    }

    private InvoiceReturnItemDTO toInvoiceReturnItemDTO(InvoiceReturnItemEntity entity) {
        InvoiceReturnItemDTO dto = new InvoiceReturnItemDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getItem() != null) {
            dto.setItemId(entity.getItem().getItemId());
            dto.setItemName(entity.getItem().getItemName());
            dto.setItemCode(entity.getItem().getItemCode());
            dto.setItemUnit(entity.getItem().getUnit());
        }
        if (entity.getInvoiceItem() != null) {
            dto.setInvoiceItemId(entity.getInvoiceItem().getInvoiceItemId());
        }
        return dto;
    }

    private InvoiceReturnEntity toInvoiceReturnEntity(InvoiceRecordEntity invoice, InvoiceReturnRequestDTO dto) {
        InvoiceReturnEntity entity = new InvoiceReturnEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setInvoice(invoice);
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
            for (InvoiceReturnItemRequestDTO item : dto.getItems()) {
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

    private InvoiceReturnItemEntity toInvoiceReturnItemEntity(InvoiceReturnEntity invoiceReturn, InvoiceItemEntity original, InvoiceReturnItemRequestDTO dto) {
        InvoiceReturnItemEntity entity = new InvoiceReturnItemEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setInvoiceReturn(invoiceReturn);
        entity.setInvoiceItem(original);
        if (dto.getItemId() != null) {
            entity.setItem(itemMasterRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + dto.getItemId())));
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

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private InvoiceItemDTO toInvoiceItemDTO(InvoiceItemEntity entity) {
        InvoiceItemDTO dto = new InvoiceItemDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getItem() != null) {
            dto.setItemId(entity.getItem().getItemId());
            dto.setItemName(entity.getItem().getItemName());
            dto.setItemCode(entity.getItem().getItemCode());
            dto.setItemUnit(entity.getItem().getUnit());
            if (dto.getHsnCode() == null || dto.getHsnCode().isBlank()) {
                dto.setHsnCode(entity.getItem().getHsnCode());
            }
            if (dto.getGstRate() == null) {
                dto.setGstRate(entity.getItem().getGstRate());
            }
        }
        return dto;
    }

    private InvoiceBalanceDTO toInvoiceBalanceDTO(InvoiceBalanceEntity entity) {
        InvoiceBalanceDTO dto = new InvoiceBalanceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private InvoiceBalanceDetailDTO toInvoiceBalanceDetailDTO(InvoiceBalanceEntity entity) {
        InvoiceBalanceDetailDTO dto = new InvoiceBalanceDetailDTO();
        dto.setBalanceId(entity.getBalanceId());
        dto.setInvoiceAmount(entity.getInvoiceAmount());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setPendingAmount(entity.getBalanceAmount());
        dto.setStatus(entity.getStatus());

        if (entity.getInvoice() != null) {
            dto.setInvoiceNo(entity.getInvoice().getInvoiceNo());
            dto.setInvoiceDate(entity.getInvoice().getInvoiceDate());

            if (entity.getInvoice().getCustomer() != null) {
                dto.setCustomerId(entity.getInvoice().getCustomer().getCustomerId());
                dto.setCustomerName(entity.getInvoice().getCustomer().getCustomerName());
            }

            if (entity.getInvoice().getUnit() != null) {
                dto.setUnitName(entity.getInvoice().getUnit().getUnitName());
            }
        }

        return dto;
    }

    private InvoicePaymentDTO toInvoicePaymentDTO(InvoicePaymentEntity entity) {
        InvoicePaymentDTO dto = new InvoicePaymentDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private byte[] buildInvoicePdf(InvoiceRecordDTO invoice) throws IOException {
        String html = buildInvoiceHtml(invoice);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        }
    }

    private String buildInvoiceHtml(InvoiceRecordDTO invoice) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'/>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; color: #333; }");
        html.append(".header { background: linear-gradient(90deg, #1f497d, #4f81bd); color: white; padding: 20px; }");
        html.append(".header h1 { margin: 0; font-size: 28px; }");
        html.append(".subheader { margin-top: 6px; font-size: 12px; color: #f2f2f2; }");
        html.append(".section { padding: 16px; }");
        html.append(".box { border: 1px solid #ddd; padding: 14px; margin-bottom: 12px; border-radius: 8px; }");
        html.append(".row { display: flex; justify-content: space-between; }");
        html.append(".col { width: 48%; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }");
        html.append("th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }");
        html.append("th { background: #f0f4fb; }");
        html.append(".totals td { border: none; padding: 4px 8px; }");
        html.append(".totals .label { text-align: right; font-weight: bold; }");
        html.append(".totals .value { text-align: right; width: 120px; }");
        html.append(".footer { margin-top: 24px; padding: 12px; background: #f9f9f9; border-radius: 8px; font-size: 12px; }");
        html.append("</style>");
        html.append("</head><body>");

        html.append("<div class='header'><h1>Tax Invoice</h1><div class='subheader'>Generated by GST Billing</div></div>");

        html.append("<div class='section'><div class='box row'>");
        html.append("<div class='col'><strong>Invoice Details</strong><br/>");
        html.append("Invoice No: " + safe(invoice.getInvoiceNo()) + "<br/>");
        html.append("Invoice Date: " + safe(invoice.getInvoiceDate()) + "<br/>");
        html.append("Place of Supply: " + safe(invoice.getPlaceOfSupply()) + "<br/>");
        html.append("State Code: " + safe(invoice.getStateCode()) + "<br/>");
        html.append("Reverse Charge: " + (Boolean.TRUE.equals(invoice.getReverseCharge()) ? "Yes" : "No") + "<br/>");
        html.append("</div>");

        html.append("<div class='col'><strong>Customer Details</strong><br/>");
        html.append("Customer ID: " + safe(invoice.getCustomerId()) + "<br/>");
        html.append("Customer Name: " + safe(invoice.getCustomerName()) + "<br/>");
        html.append("Unit: " + safe(invoice.getUnitName()) + "<br/>");
        html.append("Transporter: " + safe(invoice.getTransporterName()) + "<br/>");
        html.append("Narration: " + safe(invoice.getNarration()) + "<br/>");
        html.append("</div>");
        html.append("</div></div>");

        html.append("<div class='section'><div class='box'><strong>Items</strong>");
        html.append("<table><thead><tr><th>#</th><th>Description</th><th>HSN</th><th>Qty</th><th>Rate</th><th>GST%</th><th>Total</th></tr></thead><tbody>");
        int index = 1;
        for (InvoiceItemDTO item : invoice.getItems()) {
            html.append("<tr>");
            html.append("<td>" + index++ + "</td>");
            html.append("<td>" + safe(item.getItemName()) + " (" + safe(item.getItemCode()) + ")</td>");
            html.append("<td>" + safe(item.getHsnCode()) + "</td>");
            html.append("<td>" + safe(item.getQuantity()) + "</td>");
            html.append("<td>" + safe(item.getRate()) + "</td>");
            html.append("<td>" + safe(item.getGstRate()) + "%</td>");
            html.append("<td>" + safe(item.getLineTotal()) + "</td>");
            html.append("</tr>");
        }
        html.append("</tbody></table></div></div>");

        html.append("<div class='section'><div class='box'><table class='totals'>");
        html.append("<tr><td class='label'>Total Gross</td><td class='value'>" + safe(invoice.getTotalGrossAmount()) + "</td></tr>");
        html.append("<tr><td class='label'>Total Discount</td><td class='value'>" + safe(invoice.getTotalDiscount()) + "</td></tr>");
        html.append("<tr><td class='label'>Taxable Amount</td><td class='value'>" + safe(invoice.getTaxableAmount()) + "</td></tr>");
        html.append("<tr><td class='label'>CGST</td><td class='value'>" + safe(invoice.getTotalCgst()) + "</td></tr>");
        html.append("<tr><td class='label'>SGST</td><td class='value'>" + safe(invoice.getTotalSgst()) + "</td></tr>");
        html.append("<tr><td class='label'>IGST</td><td class='value'>" + safe(invoice.getTotalIgst()) + "</td></tr>");
        html.append("<tr><td class='label'>Round Off</td><td class='value'>" + safe(invoice.getRoundOff()) + "</td></tr>");
        html.append("<tr><td class='label'>Final Amount</td><td class='value'><strong>" + safe(invoice.getFinalAmount()) + "</strong></td></tr>");
        html.append("</table></div></div>");

        if (invoice.getBalance() != null) {
            html.append("<div class='section'><div class='box'><strong>Balance</strong><br/>");
            html.append("Amount: " + safe(invoice.getBalance().getInvoiceAmount()) + "<br/>");
            html.append("Paid: " + safe(invoice.getBalance().getPaidAmount()) + "<br/>");
            html.append("Due: " + safe(invoice.getBalance().getBalanceAmount()) + "<br/>");
            html.append("Status: " + safe(invoice.getBalance().getStatus()) + "<br/>");
            html.append("Due Date: " + safe(invoice.getBalance().getDueDate()) + "<br/>");
            html.append("</div></div>");
        }

        html.append("<div class='footer'>This is a generated GST invoice. Please verify all details before accepting.</div>");
        html.append("</body></html>");
        return html.toString();
    }

    private String generateNextInvoiceNo(LocalDate date) {
        String fy = computeFinancialYear(date);
        int attempts = 0;
        while (attempts < MAX_SEQUENCE_RETRIES) {
            attempts++;
            try {
                var opt = invoiceSequenceRepository.findByFy(fy);
                com.gst.billing.entity.InvoiceSequenceEntity seq;
                if (opt.isPresent()) {
                    seq = opt.get();
                    seq.setLastNumber(seq.getLastNumber() + 1);
                } else {
                    seq = new com.gst.billing.entity.InvoiceSequenceEntity();
                    seq.setFy(fy);
                    seq.setLastNumber(1);
                }
                seq = invoiceSequenceRepository.save(seq);
                int number = seq.getLastNumber();
                return String.format("INV/%s/%04d", fy, number);
            } catch (OptimisticLockingFailureException | DataIntegrityViolationException ex) {
                try {
                    Thread.sleep(10L * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                // retry
            }
        }
        throw new RuntimeException("Failed to generate invoice number after retries");
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

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }
}
