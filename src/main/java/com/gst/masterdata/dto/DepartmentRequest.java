package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequest {
    private String departmentCode;
    private String departmentName;
    private String description;
    private Boolean isActive;
    private Boolean isSystemDepartment;
}