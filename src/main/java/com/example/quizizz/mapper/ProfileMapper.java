package com.example.quizizz.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.quizizz.model.dto.profile.UpdateProfileRequest;
import com.example.quizizz.model.dto.profile.UpdateProfileResponse;
import com.example.quizizz.model.entity.User;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "dob", source = "dob")
    void updateUserFromDto(UpdateProfileRequest dto, @MappingTarget User user);

    @Mapping(target = "createdAt", source = "createdAt")
    UpdateProfileResponse toResponse(User user);
}
