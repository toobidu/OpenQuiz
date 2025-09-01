package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.role.CreateRoleRequest;
import com.example.quizizz.model.dto.role.UpdateRoleRequest;
import com.example.quizizz.model.dto.role.RoleResponse;

import java.util.List;

public interface IRoleService {
    RoleResponse create(CreateRoleRequest request);
    RoleResponse update(Long id, UpdateRoleRequest request);
    void delete(Long id);
    RoleResponse getById(Long id);
    List<RoleResponse> getAll();
}
