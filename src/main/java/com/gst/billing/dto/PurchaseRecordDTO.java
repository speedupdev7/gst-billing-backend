package com.gst.billing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PurchaseRecordDTO {
    private Long purchaseId;
    private String purchaseNo;
    private LocalDate purchaseDate;
    private Long unitId;
    private Long supplierId;
    private String placeOfSupply;
    private String stateCode;
    private BigDecimal totalGrossAmount;
    private BigDecimal totalDiscount;
    private BigDecimal taxableAmount;
    private BigDecimal totalCgst;
    private BigDecimal totalSgst;
    private BigDecimal totalIgst;
    private BigDecimal roundOff;
    private BigDecimal finalAmount;
    private String transporterName;
    private String vehicleNumber;
    private String narration;
    private Integer version;
    private List<PurchaseItemDTO> items;
    private List<PurchaseReturnDTO> returns;
}
