package com.gst.masterdata.dto;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class QualificationRequest {
    private String qualificationCode;
    private String qualificationName;
    private String description;
}
