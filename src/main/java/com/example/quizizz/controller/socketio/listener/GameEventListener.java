package com.example.quizizz.controller.socketio.listener;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.quizizz.controller.socketio.SocketIOEventHandler;
import com.example.quizizz.controller.socketio.dto.*;
import com.example.quizizz.model.dto.game.AnswerSubmitRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameEventListener {

    public void handleSubmitAnswer(SocketIOClient client, SubmitAnswerData data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null) return;

        try {
            AnswerSubmitRequest request = new AnswerSubmitRequest();
            request.setQuestionId(data.getQuestionId());
            request.setAnswerId(data.getAnswerId());
            request.setTimeTaken(data.getTimeTaken().longValue());
            
            var result = handler.getGameService().submitAnswer(data.getRoomId(), userId, request);
            
            handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                .sendEvent("answer-submitted", Map.of(
                    "userId", userId,
                    "questionId", data.getQuestionId(),
                    "isCorrect", result.getIsCorrect(),
                    "score", result.getScore()
                ));
                
            log.info("User {} submitted answer for question {} in room {}", userId, data.getQuestionId(), data.getRoomId());
            
        } catch (Exception e) {
            client.sendEvent("submit-answer-error", Map.of("message", e.getMessage()));
        }
    }

    public void handleNextQuestion(SocketIOClient client, NextQuestionData data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null) return;

        try {
            var nextQuestion = handler.getGameService().getNextQuestion(data.getRoomId());
            
            if (nextQuestion != null && nextQuestion.getQuestionId() != null) {
                handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("next-question", Map.of(
                        "question", nextQuestion,
                        "roomId", data.getRoomId()
                    ));
            } else {
                handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("game-finished", Map.of(
                        "roomId", data.getRoomId(),
                        "message", "Game completed"
                    ));
            }
            
            log.info("Next question requested by user {} in room {}", userId, data.getRoomId());
            
        } catch (Exception e) {
            client.sendEvent("next-question-error", Map.of("message", e.getMessage()));
        }
    }

    private Long getUserId(SocketIOClient client, SocketIOEventHandler handler) {
        Long userId = handler.getSessionManager().getUserId(client.getSessionId().toString());
        if (userId == null) {
            client.sendEvent("error", Map.of("message", "User not authenticated"));
        }
        return userId;
    }
}