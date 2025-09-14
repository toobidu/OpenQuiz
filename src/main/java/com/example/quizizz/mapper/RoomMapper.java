package com.example.quizizz.mapper;

import com.example.quizizz.model.dto.room.CreateRoomRequest;
import com.example.quizizz.model.dto.room.RoomResponse;
import com.example.quizizz.model.dto.room.UpdateRoomRequest;
import com.example.quizizz.model.entity.Room;
import com.example.quizizz.common.constants.RoomStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roomCode", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roomMode", expression = "java(request.getRoomMode().name())")
    @Mapping(target = "status", expression = "java(com.example.quizizz.common.constants.RoomStatus.WAITING.name())")
    Room toEntity(CreateRoomRequest request);
    
    @Mapping(target = "roomMode", expression = "java(com.example.quizizz.common.constants.RoomMode.valueOf(room.getRoomMode()))")
    @Mapping(target = "status", expression = "java(com.example.quizizz.common.constants.RoomStatus.valueOf(room.getStatus()))")
    @Mapping(target = "topicName", ignore = true)
    @Mapping(target = "ownerUsername", ignore = true)
    @Mapping(target = "currentPlayers", ignore = true)
    RoomResponse toResponse(Room room);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roomCode", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roomMode", expression = "java(request.getRoomMode() != null ? request.getRoomMode().name() : null)")
    void updateEntityFromRequest(@MappingTarget Room room, UpdateRoomRequest request);
}
