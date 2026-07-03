package com.gst.billing.controller;

import com.gst.billing.dto.PagedResponse;
import com.gst.billing.dto.PurchaseRecordDTO;
import com.gst.billing.dto.PurchaseReturnDTO;
import com.gst.billing.dto.PurchaseReturnRequestDTO;
import com.gst.billing.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping
    public PurchaseRecordDTO createPurchase(@RequestBody PurchaseRecordDTO purchaseRecordDTO) {
        return purchaseService.createPurchase(purchaseRecordDTO);
    }

    @PutMapping("/{purchaseId}")
    public PurchaseRecordDTO updatePurchase(@PathVariable Long purchaseId, @RequestBody PurchaseRecordDTO purchaseRecordDTO) {
        return purchaseService.updatePurchase(purchaseId, purchaseRecordDTO);
    }

    @GetMapping("/{purchaseId}")
    public PurchaseRecordDTO getPurchaseById(@PathVariable Long purchaseId) {
        return purchaseService.getPurchaseById(purchaseId);
    }

    @GetMapping("/search-by-number")
    public PurchaseRecordDTO getPurchaseByNumber(@RequestParam String purchaseNo) {
        return purchaseService.getPurchaseByNumber(purchaseNo);
    }

    @PostMapping("/returns")
    public PurchaseReturnDTO createPurchaseReturn(@RequestBody PurchaseReturnRequestDTO returnRequest) {
        return purchaseService.createPurchaseReturn(returnRequest.getPurchaseNo(), returnRequest);
    }

    @GetMapping("/{purchaseNo}/returns")
    public List<PurchaseReturnDTO> getPurchaseReturns(@PathVariable String purchaseNo) {
        return purchaseService.getPurchaseReturnsByPurchaseNumber(purchaseNo);
    }

    @GetMapping("/returns")
    public PagedResponse<PurchaseReturnDTO> getPurchaseReturns(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @PageableDefault(size = 20, page = 0, sort = "returnDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return purchaseService.getPurchaseReturnList(fromDate, toDate, pageable);
    }

    @GetMapping
    public List<PurchaseRecordDTO> getAllPurchases(@RequestParam(required = false) String q) {
        if (q != null && !q.trim().isEmpty()) {
            return purchaseService.searchPurchasesByNumber(q);
        }
        return purchaseService.getAllPurchases();
    }

    @DeleteMapping("/{purchaseId}")
    public void deletePurchase(@PathVariable Long purchaseId) {
        purchaseService.deletePurchase(purchaseId);
    }
}
