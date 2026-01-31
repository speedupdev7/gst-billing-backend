package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitMasterDTO {
    private Long unitId;
    private String unitName;
    private String gstin;
    private String address;
    private String state;
    private String stateCode;
    private String email;
    private String mobileNo;
}
