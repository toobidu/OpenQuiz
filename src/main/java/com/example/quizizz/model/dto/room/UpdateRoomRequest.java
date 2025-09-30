package com.example.quizizz.model.dto.room;

import com.example.quizizz.common.constants.RoomMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomRequest {
    private String roomName;
    private RoomMode roomMode;
    private Boolean isPrivate;
    private Integer maxPlayers;
    private Integer questionCount;
    private String questionType;
    private Integer countdownTime;
}