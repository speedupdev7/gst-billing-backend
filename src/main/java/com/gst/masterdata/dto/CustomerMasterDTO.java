package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerMasterDTO {
    private Long customerId;
    private String customerName;
    private String gstin;
    private String address;
    private String state;
    private String stateCode;
    private String email;
    private String mobileNo;
}
