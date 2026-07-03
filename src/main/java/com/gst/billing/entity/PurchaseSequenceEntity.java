package com.gst.billing.entity;

import com.gst.common.entity.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purchase_sequence", uniqueConstraints = @UniqueConstraint(columnNames = {"fy"}))
@Getter
@Setter
public class PurchaseSequenceEntity extends BaseMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fy", nullable = false, length = 16)
    private String fy;

    @Column(name = "last_number", nullable = false)
    private Integer lastNumber = 0;

    @Version
    @Column(name = "version")
    private Integer version;
}
