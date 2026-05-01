package com.gst.billing.service;

import com.gst.billing.dto.InvoiceRecordDTO;

import java.util.List;

public interface InvoiceService {
    InvoiceRecordDTO createInvoice(InvoiceRecordDTO invoiceRecordDTO);
    byte[] saveAndPrintInvoice(InvoiceRecordDTO invoiceRecordDTO);
    InvoiceRecordDTO updateInvoice(Long invoiceId, InvoiceRecordDTO invoiceRecordDTO);
    InvoiceRecordDTO getInvoiceById(Long invoiceId);
    InvoiceRecordDTO getInvoiceByNumber(String invoiceNo);
    List<InvoiceRecordDTO> getAllInvoices();
    List<InvoiceRecordDTO> searchInvoicesByNumber(String invoiceNoPrefix);
    void deleteInvoice(Long invoiceId);
}
