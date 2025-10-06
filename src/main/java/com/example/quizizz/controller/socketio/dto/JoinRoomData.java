package com.example.quizizz.controller.socketio.dto;

import lombok.Data;

@Data
public class JoinRoomData {
    private String roomCode;
    private Long roomId;
}