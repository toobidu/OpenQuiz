package com.example.quizizz.model.dto.room;

import com.example.quizizz.common.constants.RoomMode;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomRequest {

    private String roomName;

    private RoomMode roomMode;

    private Long topicId;

    private Boolean isPrivate;

    @Min(value = 2, message = "Max players must be at least 2")
    private Integer maxPlayers;

    private Duration timeLimit;
}
