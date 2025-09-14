package com.example.quizizz.mapper;

import com.example.quizizz.model.dto.authentication.RegisterRequest;
import com.example.quizizz.model.dto.authentication.RegisterResponse;
import com.example.quizizz.model.dto.authentication.LoginResponse;
import com.example.quizizz.model.entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "typeAccount", expression = "java(com.example.quizizz.common.constants.RoleCode.PLAYER.name())")
    @Mapping(target = "online", constant = "false")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDelete", ignore = true)
    @Mapping(target = "avatarURL", ignore = true)
    @Mapping(target = "systemFlag", ignore = true)
    @Mapping(target = "lastOnlineTime", ignore = true)
    User toEntity(RegisterRequest request);

    @Mapping(target = "userId", source = "id")
    RegisterResponse toRegisterResponse(User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "fullName", source = "user.fullName")
    LoginResponse toLoginResponse(User user, String accessToken, String refreshToken);
}
