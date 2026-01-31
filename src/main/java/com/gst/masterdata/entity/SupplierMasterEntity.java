package com.gst.masterdata.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "supplier_master")
@Getter @Setter
public class SupplierMasterEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    @Column(name = "gstin", length = 15)
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

