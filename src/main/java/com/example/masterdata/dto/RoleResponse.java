package com.example.masterdata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponse {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private Boolean isActive;
    private String description;
    private String roleType;
}
