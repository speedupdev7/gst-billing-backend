package com.example.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityRequest {
    private String cityCode;
    private String cityName;
    private String stateName;
    private String country;
    private Boolean isActive;
}
