package com.gst.billing.service;

import com.gst.billing.dto.InvoiceBalanceDetailDTO;
import com.gst.billing.dto.InvoiceRecordDTO;
import com.gst.billing.dto.InvoiceReturnDTO;
import com.gst.billing.dto.InvoiceReturnRequestDTO;
import com.gst.billing.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
    InvoiceRecordDTO createInvoice(InvoiceRecordDTO invoiceRecordDTO);
    byte[] saveAndPrintInvoice(InvoiceRecordDTO invoiceRecordDTO);
    InvoiceRecordDTO updateInvoice(Long invoiceId, InvoiceRecordDTO invoiceRecordDTO);
    InvoiceRecordDTO getInvoiceById(Long invoiceId);
    InvoiceRecordDTO getInvoiceByNumber(String invoiceNo);
    InvoiceReturnDTO createInvoiceReturn(String invoiceNo, InvoiceReturnRequestDTO returnRequest);
    List<InvoiceReturnDTO> getInvoiceReturnsByInvoiceNumber(String invoiceNo);
    List<InvoiceRecordDTO> getAllInvoices();
    List<InvoiceRecordDTO> searchInvoicesByNumber(String invoiceNoPrefix);
    void deleteInvoice(Long invoiceId);
    List<InvoiceBalanceDetailDTO> getAllInvoiceBalances();
    List<InvoiceBalanceDetailDTO> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate);
    PagedResponse<InvoiceBalanceDetailDTO> getAllInvoiceBalancesPageable(Pageable pageable);
    PagedResponse<InvoiceBalanceDetailDTO> getInvoicesByDateRangePageable(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
