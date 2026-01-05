package com.example.masterdata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employee_master")
@Getter @Setter
public class EmployeeMasterEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "designation_id", nullable = false)
    private DesignationMasterEntity designation;

    @ManyToOne
    @JoinColumn(name = "qualification_id")
    private QualificationMasterEntity qualification;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityMasterEntity city;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleMasterEntity role;
}

