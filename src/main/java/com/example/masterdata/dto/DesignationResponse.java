package com.example.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DesignationResponse {
    private Long designationId;
    private String designationName;
    private String designationCode;
    private Boolean isActive;
}
