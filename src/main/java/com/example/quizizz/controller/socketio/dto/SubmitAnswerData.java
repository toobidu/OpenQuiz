package com.example.quizizz.controller.socketio.dto;

import lombok.Data;

@Data
public class SubmitAnswerData {
    private Long roomId;
    private Long questionId;
    private Long answerId;
    private Integer timeTaken;
}