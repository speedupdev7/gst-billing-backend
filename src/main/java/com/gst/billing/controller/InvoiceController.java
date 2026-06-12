package com.gst.billing.controller;

import com.gst.billing.dto.InvoiceBalanceDetailDTO;
import com.gst.billing.dto.InvoiceRecordDTO;
import com.gst.billing.dto.InvoiceReturnDTO;
import com.gst.billing.dto.InvoiceReturnListDTO;
import com.gst.billing.dto.InvoiceReturnRequestDTO;
import com.gst.billing.dto.PagedResponse;
import com.gst.billing.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/save-and-print")
    public ResponseEntity<byte[]> saveAndPrintInvoice(@RequestBody InvoiceRecordDTO invoiceRecordDTO) {
        byte[] pdfBytes = invoiceService.saveAndPrintInvoice(invoiceRecordDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PutMapping("/{invoiceId}")
    public InvoiceRecordDTO updateInvoice(@PathVariable Long invoiceId, @RequestBody InvoiceRecordDTO invoiceRecordDTO) {
        return invoiceService.updateInvoice(invoiceId, invoiceRecordDTO);
    }

    @GetMapping("/{invoiceId}")
    public InvoiceRecordDTO getInvoiceById(@PathVariable Long invoiceId) {
        return invoiceService.getInvoiceById(invoiceId);
    }

    @GetMapping("/search-by-number")
    public InvoiceRecordDTO getInvoiceByNumber(@RequestParam String invoiceNo) {
        return invoiceService.getInvoiceByNumber(invoiceNo);
    }

    @PostMapping("/returns")
    public InvoiceReturnDTO createInvoiceReturn(@RequestBody InvoiceReturnRequestDTO returnRequest) {
        return invoiceService.createInvoiceReturn(returnRequest.getInvoiceNo(), returnRequest);
    }

    @GetMapping("/{invoiceNo}/returns")
    public List<InvoiceReturnDTO> getInvoiceReturns(@PathVariable String invoiceNo) {
        return invoiceService.getInvoiceReturnsByInvoiceNumber(invoiceNo);
    }

    @GetMapping("/returns")
    public PagedResponse<InvoiceReturnListDTO> getInvoiceReturns(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @PageableDefault(size = 20, page = 0, sort = "returnDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return invoiceService.getInvoiceReturnList(fromDate, toDate, pageable);
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

    @GetMapping("/balance/all")
    public List<InvoiceBalanceDetailDTO> getAllInvoiceBalances() {
        return invoiceService.getAllInvoiceBalances();
    }

    @GetMapping("/balance/date-range")
    public List<InvoiceBalanceDetailDTO> getInvoicesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return invoiceService.getInvoicesByDateRange(startDate, endDate);
    }

    @GetMapping("/balance/all-paginated")
    public PagedResponse<InvoiceBalanceDetailDTO> getAllInvoiceBalancesPaginated(
            @PageableDefault(size = 20, page = 0, sort = "invoice.invoiceDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return invoiceService.getAllInvoiceBalancesPageable(pageable);
    }

    @GetMapping("/balance/date-range-paginated")
    public PagedResponse<InvoiceBalanceDetailDTO> getInvoicesByDateRangePaginated(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @PageableDefault(size = 20, page = 0, sort = "invoice.invoiceDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return invoiceService.getInvoicesByDateRangePageable(startDate, endDate, pageable);
    }
}
