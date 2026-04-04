package com.gst.masterdata.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerMasterDTO {
    private Long customerId;
    private String customerName;
    private String gstin;
    private String state;
    private String stateCode;
    private String email;
    private String mobileNo;
    //B2B, B2C, CUSTOMER1
    private String customerType;
    private String pinCode;
    private String district;
    private String billingAddress;
}
