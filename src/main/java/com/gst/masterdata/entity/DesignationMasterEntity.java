package com.gst.masterdata.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "master_designation")
@Getter @Setter
public class DesignationMasterEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designation_id")
    private Long designationId;

    @Column(name = "designation_code", nullable = false, unique = true)
    private String designationCode;

    @Column(name = "designation_name", nullable = false)
    private String designationName;

    @Column(name = "description")
    private String description;
}

