package com.gst.masterdata.dto;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class QualificationResponse {
    private Long qualificationId;
    private String qualificationCode;
    private String qualificationName;
    private String description;
    private Boolean isActive;
}
