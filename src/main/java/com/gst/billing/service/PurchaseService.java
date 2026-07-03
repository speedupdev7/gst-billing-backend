package com.gst.billing.service;

import com.gst.billing.dto.PagedResponse;
import com.gst.billing.dto.PurchaseRecordDTO;
import com.gst.billing.dto.PurchaseReturnDTO;
import com.gst.billing.dto.PurchaseReturnRequestDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseService {
    PurchaseRecordDTO createPurchase(PurchaseRecordDTO purchaseRecordDTO);
    PurchaseRecordDTO updatePurchase(Long purchaseId, PurchaseRecordDTO purchaseRecordDTO);
    PurchaseRecordDTO getPurchaseById(Long purchaseId);
    PurchaseRecordDTO getPurchaseByNumber(String purchaseNo);
    PurchaseReturnDTO createPurchaseReturn(String purchaseNo, PurchaseReturnRequestDTO returnRequest);
    List<PurchaseReturnDTO> getPurchaseReturnsByPurchaseNumber(String purchaseNo);
    PagedResponse<PurchaseReturnDTO> getPurchaseReturnList(LocalDate fromDate, LocalDate toDate, Pageable pageable);
    List<PurchaseRecordDTO> getAllPurchases();
    List<PurchaseRecordDTO> searchPurchasesByNumber(String purchaseNoPrefix);
    void deletePurchase(Long purchaseId);
}
