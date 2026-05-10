package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceReturnRequestDTO {
    private String returnNo;
    private LocalDate returnDate;
    private String returnType;
    private String reasonCode;
    private String reasonText;
    private String remarks;
    private List<InvoiceReturnItemRequestDTO> items;
}
