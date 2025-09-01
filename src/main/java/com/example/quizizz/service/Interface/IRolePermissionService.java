package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.role.AssignPermissionsToRoleRequest;
import com.example.quizizz.model.dto.permission.AssignRolesToPermissionRequest;

import java.util.Set;

public interface IRolePermissionService {
    void assignPermissionsToRole(AssignPermissionsToRoleRequest request);
    void removePermissionsFromRole(AssignPermissionsToRoleRequest request);
    void assignRolesToPermission(AssignRolesToPermissionRequest request);
    void removeRolesFromPermission(AssignRolesToPermissionRequest request);
    void refreshUserPermissionsCache(Set<Long> userIds);
}
