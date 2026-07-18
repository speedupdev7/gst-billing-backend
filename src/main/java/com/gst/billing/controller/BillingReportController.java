package com.gst.billing.controller;

import com.gst.billing.dto.BillingReportDTO;
import com.gst.billing.dto.InvoiceRecordDTO;
import com.gst.billing.dto.PagedResponse;
import com.gst.billing.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class BillingReportController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/billing")
    public BillingReportDTO getBillingReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        List<InvoiceRecordDTO> invoices = invoiceService.getAllInvoices();

        if (fromDate != null && toDate != null) {
            invoices = invoices.stream()
                    .filter(i -> {
                        LocalDate d = i.getInvoiceDate();
                        return d != null && (!d.isBefore(fromDate)) && (!d.isAfter(toDate));
                    })
                    .collect(Collectors.toList());
        }

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;

        for (InvoiceRecordDTO inv : invoices) {
            if (inv.getTotalGrossAmount() != null) totalGross = totalGross.add(inv.getTotalGrossAmount());
            if (inv.getTotalDiscount() != null) totalDiscount = totalDiscount.add(inv.getTotalDiscount());
            BigDecimal gst = BigDecimal.ZERO;
            if (inv.getTotalCgst() != null) gst = gst.add(inv.getTotalCgst());
            if (inv.getTotalSgst() != null) gst = gst.add(inv.getTotalSgst());
            if (inv.getTotalIgst() != null) gst = gst.add(inv.getTotalIgst());
            totalGst = totalGst.add(gst);
            if (inv.getFinalAmount() != null) totalNet = totalNet.add(inv.getFinalAmount());
        }

        BillingReportDTO dto = new BillingReportDTO();
        dto.setInvoices(invoices);
        dto.setTotalGross(totalGross);
        dto.setTotalDiscount(totalDiscount);
        dto.setTotalGst(totalGst);
        dto.setTotalNet(totalNet);
        return dto;
    }

    @GetMapping("/billing/paginated")
    public BillingReportPagedDTO getBillingReportPaginated(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String billStatus,
            @RequestParam(required = false) String paymentMode,
            @org.springframework.data.web.PageableDefault(size = 20, page = 0) org.springframework.data.domain.Pageable pageable
    ) {
        List<InvoiceRecordDTO> invoices = invoiceService.getAllInvoices();

        // date filter
        if (fromDate != null && toDate != null) {
            invoices = invoices.stream()
                    .filter(i -> {
                        LocalDate d = i.getInvoiceDate();
                        return d != null && (!d.isBefore(fromDate)) && (!d.isAfter(toDate));
                    })
                    .collect(Collectors.toList());
        }

        // bill status filter (checks balance.status)
        if (billStatus != null && !billStatus.trim().isEmpty()) {
            String bs = billStatus.trim().toLowerCase();
            invoices = invoices.stream()
                    .filter(i -> i.getBalance() != null && i.getBalance().getStatus() != null && i.getBalance().getStatus().toLowerCase().contains(bs))
                    .collect(Collectors.toList());
        }

        // payment mode filter (checks payments)
        if (paymentMode != null && !paymentMode.trim().isEmpty()) {
            String pm = paymentMode.trim().toLowerCase();
            invoices = invoices.stream()
                    .filter(i -> i.getPayments() != null && i.getPayments().stream().anyMatch(p -> p.getPaymentMode() != null && p.getPaymentMode().toLowerCase().contains(pm)))
                    .collect(Collectors.toList());
        }

        // totals across filtered set
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;

        for (InvoiceRecordDTO inv : invoices) {
            if (inv.getTotalGrossAmount() != null) totalGross = totalGross.add(inv.getTotalGrossAmount());
            if (inv.getTotalDiscount() != null) totalDiscount = totalDiscount.add(inv.getTotalDiscount());
            BigDecimal gst = BigDecimal.ZERO;
            if (inv.getTotalCgst() != null) gst = gst.add(inv.getTotalCgst());
            if (inv.getTotalSgst() != null) gst = gst.add(inv.getTotalSgst());
            if (inv.getTotalIgst() != null) gst = gst.add(inv.getTotalIgst());
            totalGst = totalGst.add(gst);
            if (inv.getFinalAmount() != null) totalNet = totalNet.add(inv.getFinalAmount());
        }

        // build page
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int total = invoices.size();
        int start = pageNumber * pageSize;
        List<InvoiceRecordDTO> pageContent;
        if (start >= total) {
            pageContent = java.util.Collections.emptyList();
        } else {
            int end = Math.min(start + pageSize, total);
            pageContent = invoices.subList(start, end);
        }

        com.gst.billing.dto.PagedResponse<InvoiceRecordDTO> paged = new com.gst.billing.dto.PagedResponse<>();
        paged.setContent(pageContent);
        paged.setPageNumber(pageNumber);
        paged.setPageSize(pageSize);
        paged.setTotalElements(total);
        paged.setTotalPages((int) Math.ceil((double) total / (double) pageSize));
        paged.setLast((pageNumber + 1) * pageSize >= total);
        paged.setFirst(pageNumber == 0);
        paged.setNumberOfElements(pageContent.size());
        paged.setEmpty(pageContent.isEmpty());

        BillingReportPagedDTO out = new BillingReportPagedDTO();
        out.setInvoicesPage(paged);
        out.setTotalGross(totalGross);
        out.setTotalDiscount(totalDiscount);
        out.setTotalGst(totalGst);
        out.setTotalNet(totalNet);
        return out;
    }

    @GetMapping("/returns/paginated")
    public com.gst.billing.dto.BillingReturnReportPagedDTO getBillingReturnReportPaginated(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String returnStatus,
            @RequestParam(required = false) String returnReason,
            @org.springframework.data.web.PageableDefault(size = 20, page = 0) org.springframework.data.domain.Pageable pageable
    ) {
        // paged content from existing service
        PagedResponse<com.gst.billing.dto.InvoiceReturnListDTO> page = invoiceService.getInvoiceReturnList(fromDate, toDate, pageable);

        // totals computed over full filtered set
        com.gst.billing.dto.InvoiceReturnTotalsDTO totals = invoiceService.getInvoiceReturnTotals(fromDate, toDate, returnStatus, returnReason);

        com.gst.billing.dto.BillingReturnReportPagedDTO out = new com.gst.billing.dto.BillingReturnReportPagedDTO();
        out.setReturnsPage(page);
        out.setTotalBase(totals.getTotalBase());
        out.setTotalTax(totals.getTotalTax());
        out.setTotalRefund(totals.getTotalRefund());
        return out;
    }
}
