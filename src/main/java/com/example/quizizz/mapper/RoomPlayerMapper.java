package com.example.quizizz.mapper;

import com.example.quizizz.model.dto.room.RoomPlayerResponse;
import com.example.quizizz.model.entity.RoomPlayers;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomPlayerMapper {

    @Mapping(target = "joinedAt", source = "createdAt")
    @Mapping(target = "username", ignore = true)
    RoomPlayerResponse toResponse(RoomPlayers roomPlayer);
}
