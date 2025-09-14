package com.example.quizizz.mapper;

import com.example.quizizz.model.dto.permission.CreatePermissionRequest;
import com.example.quizizz.model.dto.permission.PermissionResponse;
import com.example.quizizz.model.dto.permission.UpdatePermissionRequest;
import com.example.quizizz.model.entity.Permission;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "permissionName", source = "permissionName")
    @Mapping(target = "description", source = "description")
    Permission toEntity(CreatePermissionRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "permissionName", source = "permissionName")
    @Mapping(target = "description", source = "description")
    void updateEntityFromDto(UpdatePermissionRequest dto, @MappingTarget Permission entity);

    PermissionResponse toResponse(Permission entity);
}
