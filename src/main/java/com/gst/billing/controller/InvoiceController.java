package com.gst.billing.controller;

import com.gst.billing.dto.InvoiceRecordDTO;
import com.gst.billing.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public InvoiceRecordDTO createInvoice(@RequestBody InvoiceRecordDTO invoiceRecordDTO) {
        return invoiceService.createInvoice(invoiceRecordDTO);
    }

    @PutMapping("/{invoiceId}")
    public InvoiceRecordDTO updateInvoice(@PathVariable Long invoiceId, @RequestBody InvoiceRecordDTO invoiceRecordDTO) {
        return invoiceService.updateInvoice(invoiceId, invoiceRecordDTO);
    }

    @GetMapping("/{invoiceId}")
    public InvoiceRecordDTO getInvoiceById(@PathVariable Long invoiceId) {
        return invoiceService.getInvoiceById(invoiceId);
    }

    @GetMapping
    public List<InvoiceRecordDTO> getAllInvoices(@RequestParam(required = false) String q) {
        if (q != null && !q.trim().isEmpty()) {
            return invoiceService.searchInvoicesByNumber(q);
        }
        return invoiceService.getAllInvoices();
    }

    @DeleteMapping("/{invoiceId}")
    public void deleteInvoice(@PathVariable Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
    }
}
