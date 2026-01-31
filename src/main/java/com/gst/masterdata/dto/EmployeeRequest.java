package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeRequest {
    private String employeeCode;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private LocalDate joiningDate;
    private String mobileNo;
    private String emailId;
    private String designationName;
    private String panNumber;
    private String aadhaarNumber;
    private String address;
    private Long designationId;
    private Long qualifictionId;
    private Long cityId;
    private Long roleId;
    private LocalDate dateOfBirth;
    private Boolean isActive;
}
