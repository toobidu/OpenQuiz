package com.example.quizizz.mapper;

import com.example.quizizz.model.dto.role.CreateRoleRequest;
import com.example.quizizz.model.dto.role.UpdateRoleRequest;
import com.example.quizizz.model.dto.role.RoleResponse;
import com.example.quizizz.model.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toResponse(Role role);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "roleName", expression = "java(request.getRoleName().trim().toUpperCase())")
    @Mapping(target = "description", source = "description")
    Role toEntity(CreateRoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    @Mapping(target = "roleName", expression = "java(request.getRoleName() != null ? request.getRoleName().trim().toUpperCase() : role.getRoleName())")
    @Mapping(target = "description", source = "description")
    void updateEntityFromDto(UpdateRoleRequest request, @MappingTarget Role role);
}