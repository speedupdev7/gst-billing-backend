package com.example.masterdata.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class EmployeeResponse {
    private Long employeeId;
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
    private Long designnationId;
    private Long qualifictionId;
    private Long cityId;
    private Long roleId;
    private LocalDate dateOfBirth;
    private Boolean isActive;
}
