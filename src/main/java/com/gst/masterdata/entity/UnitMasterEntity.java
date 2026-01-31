package com.gst.masterdata.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "unit_master")
@Getter
@Setter
public class UnitMasterEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "unit_name", nullable = false)
    private String unitName;

    @Column(name = "gstin", length = 15, unique = true)
    private String gstin;

    @Column(name = "address")
    private String address;

    @Column(name = "state")
    private String state;

    @Column(name = "state_code", length = 5)
    private String stateCode;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_no", length = 15)
    private String mobileNo;
}

