package com.gst.masterdata.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "master_qualification")
@Getter @Setter
public class QualificationMasterEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qualification_id")
    private Long qualificationId;

    @Column(name = "qualification_code", nullable = false, unique = true)
    private String qualificationCode;

    @Column(name = "qualification_name", nullable = false)
    private String qualificationName;

    @Column(name = "description")
    private String description;
}

