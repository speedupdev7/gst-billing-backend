package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DesignationRequest {
    private String designationCode;
    private String designationName;
    private String description;
}
