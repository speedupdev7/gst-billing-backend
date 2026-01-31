package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierMasterDTO {
    private Long supplierId;
    private String supplierName;
    private String gstin;
    private String address;
    private String state;
    private String stateCode;
    private String email;
    private String mobileNo;
}
