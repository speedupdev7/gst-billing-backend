package com.gst.billing.service.impl;

import com.gst.billing.dto.InvoiceBalanceDTO;
import com.gst.billing.dto.InvoiceItemDTO;
import com.gst.billing.dto.InvoicePaymentDTO;
import com.gst.billing.dto.InvoiceRecordDTO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        InvoiceRecordEntity invoiceRecordEntity = toInvoiceRecordEntity(invoiceRecordDTO);
        InvoiceRecordEntity savedInvoice = invoiceRecordRepository.save(invoiceRecordEntity);

        List<InvoiceItemEntity> savedItems = saveInvoiceItems(savedInvoice, invoiceRecordDTO.getItems());
        InvoiceBalanceEntity savedBalance = saveInvoiceBalance(savedInvoice, invoiceRecordDTO.getBalance());
        List<InvoicePaymentEntity> savedPayments = saveInvoicePayments(savedInvoice, invoiceRecordDTO.getPayments());

        return toInvoiceRecordDTO(savedInvoice, savedItems, savedBalance, savedPayments);
    }

    @Override
    @Transactional
    public InvoiceRecordDTO updateInvoice(Long invoiceId, InvoiceRecordDTO invoiceRecordDTO) {
        InvoiceRecordEntity existingInvoice = invoiceRecordRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

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

    private void validateInvoiceRequest(InvoiceRecordDTO invoiceRecordDTO) {
        if (invoiceRecordDTO == null) {
            throw new IllegalArgumentException("Invoice request cannot be null");
        }
        if (invoiceRecordDTO.getItems() == null || invoiceRecordDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must contain at least one item");
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

        return entity;
    }

    private InvoiceItemEntity toInvoiceItemEntity(InvoiceRecordEntity invoice, InvoiceItemDTO dto) {
        InvoiceItemEntity entity = new InvoiceItemEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setInvoice(invoice);

        if (dto.getItemId() != null) {
            ItemMasterEntity item = itemMasterRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + dto.getItemId()));
            entity.setItem(item);
        }

        return entity;
    }

    private List<InvoiceItemEntity> saveInvoiceItems(InvoiceRecordEntity invoice, List<InvoiceItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        List<InvoiceItemEntity> entities = items.stream()
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
        }
        if (entity.getUnit() != null) {
            dto.setUnitId(entity.getUnit().getUnitId());
        }
        return dto;
    }

    private InvoiceItemDTO toInvoiceItemDTO(InvoiceItemEntity entity) {
        InvoiceItemDTO dto = new InvoiceItemDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getItem() != null) {
            dto.setItemId(entity.getItem().getItemId());
        }
        return dto;
    }

    private InvoiceBalanceDTO toInvoiceBalanceDTO(InvoiceBalanceEntity entity) {
        InvoiceBalanceDTO dto = new InvoiceBalanceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private InvoicePaymentDTO toInvoicePaymentDTO(InvoicePaymentEntity entity) {
        InvoicePaymentDTO dto = new InvoicePaymentDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
