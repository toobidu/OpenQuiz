package com.example.quizizz.controller.socketio.listener;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.quizizz.controller.socketio.SocketIOEventHandler;
import com.example.quizizz.controller.socketio.dto.*;
import com.example.quizizz.model.dto.game.AnswerSubmitRequest;
import com.example.quizizz.model.dto.game.GameStateResponse;
import com.example.quizizz.model.dto.game.NextQuestionResponse;
import com.example.quizizz.model.dto.room.RoomPlayerResponse;
import com.example.quizizz.model.dto.room.RoomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                    "roomId", data.getRoomId(),
                    "userId", userId,
                    "questionId", data.getQuestionId(),
                    "isCorrect", result.getIsCorrect(),
                    "score", result.getScore(),
                    "correctAnswerId", result.getCorrectAnswerId()
                ));

            log.info("User {} submitted answer for question {} in room {}", userId, data.getQuestionId(), data.getRoomId());

        } catch (Exception e) {
            client.sendEvent("submit-answer-error", Map.of("message", e.getMessage()));
        }
    }

    public void handleSubmitAnswerFront(SocketIOClient client, SubmitAnswerFrontDto data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null) return;
        try {
            RoomResponse room = handler.getRoomService().getRoomByCode(data.getRoomCode());
            Long roomId = room.getId();
            Long answerId = handler.getGameService().resolveAnswerId(
                    data.getQuestionId(), data.getSelectedOptionIndex(), data.getSelectedAnswer(), data.getAnswerText());

            AnswerSubmitRequest request = new AnswerSubmitRequest();
            request.setQuestionId(data.getQuestionId());
            request.setAnswerId(answerId);
            Long timeTaken = data.getSubmissionTime() != null ? data.getSubmissionTime() : 0L;
            request.setTimeTaken(timeTaken);

            var result = handler.getGameService().submitAnswer(roomId, userId, request);

            handler.getSocketIOServer().getRoomOperations("room-" + roomId)
                .sendEvent("answer-submitted", Map.of(
                    "roomId", roomId,
                    "userId", userId,
                    "questionId", data.getQuestionId(),
                    "isCorrect", result.getIsCorrect(),
                    "score", result.getScore(),
                    "correctAnswerId", result.getCorrectAnswerId()
                ));

            log.info("[submitAnswer] user {} answered q {} in room {}", userId, data.getQuestionId(), roomId);
        } catch (Exception e) {
            client.sendEvent("submit-answer-error", Map.of("message", e.getMessage()));
        }
    }

    public void handleGetGameState(SocketIOClient client, GetGameStateData data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null) return;
        try {
            RoomResponse room = handler.getRoomService().getRoomByCode(data.getRoomCode());
            Long roomId = room.getId();

            // Build a minimal GameStateResponse snapshot
            GameStateResponse state = new GameStateResponse();
            state.setRoomId(roomId);
            state.setGameStatus(room.getStatus().name());
            state.setTotalQuestions(room.getQuestionCount());
            state.setCurrentQuestionIndex(0);
            state.setTimeRemaining((long) handler.getGameService().getRemainingTime(roomId));

            List<RoomPlayerResponse> players = handler.getRoomService().getRoomPlayers(roomId);
            List<GameStateResponse.PlayerGameState> playerStates = players.stream().map(p -> {
                GameStateResponse.PlayerGameState ps = new GameStateResponse.PlayerGameState();
                ps.setUserId(p.getUserId());
                ps.setUsername(p.getUsername());
                ps.setDisplayName(p.getUsername());
                ps.setAvatarUrl(null);
                ps.setScore(0);
                ps.setHasAnswered(false);
                ps.setStatus("ACTIVE");
                ps.setCorrectAnswers(0);
                ps.setTotalAnswers(0);
                return ps;
            }).collect(Collectors.toList());
            state.setPlayers(playerStates);
            state.setIsHost(room.getOwnerId().equals(userId));

            client.sendEvent("game-state", Map.of(
                    "roomId", roomId,
                    "gameState", state
            ));
        } catch (Exception e) {
            client.sendEvent("get-game-state-error", Map.of("message", e.getMessage()));
        }
    }

    public void handleNextQuestion(SocketIOClient client, NextQuestionData data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null) return;

        try {
            NextQuestionResponse nextQuestion = handler.getGameService().getNextQuestion(data.getRoomId());

            if (nextQuestion != null && nextQuestion.getQuestionId() != null) {
                // Flatten payload per spec
                handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("next-question", Map.of(
                        "roomId", data.getRoomId(),
                        "questionId", nextQuestion.getQuestionId(),
                        "questionText", nextQuestion.getQuestionText(),
                        "answers", nextQuestion.getAnswers().stream().map(a -> Map.of(
                            "id", a.getId(),
                            "text", a.getText()
                        )).collect(Collectors.toList()),
                        "imageUrl", null,
                        "timeLimit", nextQuestion.getTimeLimit(),
                        "currentQuestionNumber", nextQuestion.getQuestionNumber(),
                        "totalQuestions", nextQuestion.getTotalQuestions()
                    ));
            } else {
                handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("game-ended", Map.of(
                        "roomId", data.getRoomId(),
                        "gameState", Map.of("gameStatus", "FINISHED")
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