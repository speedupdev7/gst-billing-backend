package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentResponse {
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private Boolean isActive;
    private String description;
}