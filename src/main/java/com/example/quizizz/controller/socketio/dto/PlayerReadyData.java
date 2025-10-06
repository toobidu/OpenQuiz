package com.example.quizizz.controller.socketio.dto;

import lombok.Data;

@Data
public class PlayerReadyData {
    private Long roomId;
    private Boolean isReady;
}
