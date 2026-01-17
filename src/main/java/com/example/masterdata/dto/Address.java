package com.example.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    @Override
    public String toString() {
        return street + ", "
                + city + ", "
                + state + " "
                + postalCode + ", "
                + country;
    }
}
