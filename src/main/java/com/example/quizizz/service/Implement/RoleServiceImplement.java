package com.example.quizizz.service.Implement;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quizizz.common.constants.MessageCode;
import com.example.quizizz.common.exception.ApiException;
import com.example.quizizz.mapper.RoleMapper;
import com.example.quizizz.model.dto.role.CreateRoleRequest;
import com.example.quizizz.model.dto.role.RoleResponse;
import com.example.quizizz.model.dto.role.UpdateRoleRequest;
import com.example.quizizz.model.entity.Role;
import com.example.quizizz.repository.RoleRepository;
import com.example.quizizz.service.Interface.IRoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImplement implements IRoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public RoleResponse create(CreateRoleRequest request) {
        String normalizedRoleName = request.getRoleName().trim().toUpperCase();
        if (roleRepository.findByRoleName(normalizedRoleName).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT.value(), MessageCode.ROLE_ALREADY_EXISTS, "Role name already exists");
        }
        Role role = roleMapper.toEntity(request);
        role.setRoleName(normalizedRoleName);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public RoleResponse update(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));
        if (request.getRoleName() != null) {
            String normalizedRoleName = request.getRoleName().trim().toUpperCase();
            if (!normalizedRoleName.equals(role.getRoleName()) &&
                roleRepository.findByRoleName(normalizedRoleName).isPresent()) {
                throw new ApiException(HttpStatus.CONFLICT.value(), MessageCode.ROLE_ALREADY_EXISTS, "Role name already exists");
            }
            role.setRoleName(normalizedRoleName);
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));
        roleRepository.delete(role);
    }

    @Override
    @Cacheable(value = "role", key = "#id")
    public RoleResponse getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));
        return roleMapper.toResponse(role);
    }

    @Override
    @Cacheable("roles")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .toList();
    }
}
