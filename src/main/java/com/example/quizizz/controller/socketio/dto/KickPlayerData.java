package com.example.quizizz.controller.socketio.dto;

import lombok.Data;

@Data
public class KickPlayerData {
    private Long roomId;
    private Long playerId;
    private String reason;
}