package com.gst.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
    private String roleCode;
    private String roleName;
    private String description;
    private Boolean isActive;
    private Boolean isSystemRole;
}
