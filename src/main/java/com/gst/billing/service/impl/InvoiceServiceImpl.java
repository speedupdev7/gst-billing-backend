package com.gst.billing.service.impl;

import com.gst.billing.dto.InvoiceBalanceDTO;
import com.gst.billing.dto.InvoiceBalanceDetailDTO;
import com.gst.billing.dto.InvoiceItemDTO;
import com.gst.billing.dto.InvoicePaymentDTO;
import com.gst.billing.dto.InvoiceRecordDTO;
import com.gst.billing.dto.PagedResponse;
import com.gst.billing.entity.InvoiceBalanceEntity;
import com.gst.billing.entity.InvoiceItemEntity;
import com.gst.billing.entity.InvoicePaymentEntity;
import com.gst.billing.entity.InvoiceRecordEntity;
import com.gst.billing.repository.InvoiceBalanceRepository;
import com.gst.billing.repository.InvoiceItemRepository;
import com.gst.billing.repository.InvoicePaymentRepository;
import com.gst.billing.repository.InvoiceRecordRepository;
import com.gst.billing.service.InvoiceService;
import com.gst.masterdata.entity.CustomerMasterEntity;
import com.gst.masterdata.entity.ItemMasterEntity;
import com.gst.masterdata.entity.UnitMasterEntity;
import com.gst.masterdata.exceptions.ResourceNotFoundException;
import com.gst.masterdata.repository.CustomerMasterRepository;
import com.gst.masterdata.repository.ItemMasterRepository;
import com.gst.masterdata.repository.UnitMasterRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private InvoiceRecordRepository invoiceRecordRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private InvoicePaymentRepository invoicePaymentRepository;

    @Autowired
    private InvoiceBalanceRepository invoiceBalanceRepository;

    @Autowired
    private CustomerMasterRepository customerMasterRepository;

    @Autowired
    private ItemMasterRepository itemMasterRepository;

    @Autowired
    private UnitMasterRepository unitMasterRepository;

    @Override
    @Transactional
    public InvoiceRecordDTO createInvoice(InvoiceRecordDTO invoiceRecordDTO) {
        validateInvoiceRequest(invoiceRecordDTO);
        preparePaymentsAndBalance(invoiceRecordDTO);
        InvoiceRecordEntity invoiceRecordEntity = toInvoiceRecordEntity(invoiceRecordDTO);
        InvoiceRecordEntity savedInvoice = invoiceRecordRepository.save(invoiceRecordEntity);

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
            CustomerMasterEntity customer = customerMasterRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));
            entity.setCustomer(customer);
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
                entity.setBatchCode(item.getBatchCode());
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
        return dto;
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
        dto.setStatus(entity.getStatus());

        if (entity.getInvoice() != null) {
            dto.setInvoiceNo(entity.getInvoice().getInvoiceNo());
            dto.setInvoiceDate(entity.getInvoice().getInvoiceDate());

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

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }
}
