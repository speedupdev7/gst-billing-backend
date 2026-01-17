package com.example.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityResponse {
    private Long cityId;
    private String cityCode;
    private String cityName;
    private String stateName;
    private String country;
    private Boolean isActive;
}
