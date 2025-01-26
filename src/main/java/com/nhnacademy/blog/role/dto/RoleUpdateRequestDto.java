package com.nhnacademy.blog.role.dto;

public class RoleUpdateRequestDto {
    private final String roleId;
    private final String roleName;
    private final String roleDescription;

    public RoleUpdateRequestDto(String roleId, String roleName, String roleDescription) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }
}
