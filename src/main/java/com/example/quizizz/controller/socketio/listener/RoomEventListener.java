package com.example.quizizz.controller.socketio.listener;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.quizizz.controller.socketio.SocketIOEventHandler;
import com.example.quizizz.controller.socketio.dto.*;
import com.example.quizizz.model.dto.room.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomEventListener {

    public void handleJoinRoom(SocketIOClient client, JoinRoomData data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null)
            return;

        try {
            if (data.getRoomId() != null) {
                // Join room in database first
                var roomResponse = handler.getRoomService().joinRoomById(data.getRoomId(), userId);
                
                // Join Socket.IO room
                client.joinRoom("room-" + data.getRoomId());
                handler.getSessionManager().addRoomSession(client.getSessionId().toString(), data.getRoomId());

                String username = handler.getUserRepository().findById(userId)
                        .map(user -> user.getUsername()).orElse("Unknown");

                // Broadcast player-joined event
                handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                        .sendEvent("player-joined", Map.of(
                                "roomId", data.getRoomId(),
                                "userId", userId,
                                "username", username
                        ));

                // Broadcast updated players list
                var players = handler.getRoomService().getRoomPlayers(data.getRoomId());
                handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                        .sendEvent("room-players", Map.of(
                                "roomId", data.getRoomId(),
                                "players", players
                        ));

                // Send success response
                client.sendEvent("join-room-success", Map.of(
                        "success", true,
                        "roomId", data.getRoomId(),
                        "room", roomResponse,
                        "message", "Joined room successfully"));

                log.info("User {} joined room {} via Socket.IO", userId, data.getRoomId());
                return;
            }

            if (data.getRoomCode() != null) {
                try {
                    JoinRoomRequest request = new JoinRoomRequest();
                    request.setRoomCode(data.getRoomCode());

                    var roomResponse = handler.getRoomService().joinRoom(request, userId);

                    client.joinRoom("room-" + roomResponse.getId());
                    handler.getSessionManager().addRoomSession(client.getSessionId().toString(), roomResponse.getId());

                    String username = handler.getUserRepository().findById(userId)
                            .map(user -> user.getUsername()).orElse("Unknown");

                    // Always broadcast player-joined for Socket.IO connections
                    handler.getSocketIOServer().getRoomOperations("room-" + roomResponse.getId())
                            .sendEvent("player-joined", Map.of(
                                    "roomId", roomResponse.getId(),
                                    "userId", userId,
                                    "username", username
                            ));

                    // Always broadcast updated players list
                    var players = handler.getRoomService().getRoomPlayers(roomResponse.getId());
                    handler.getSocketIOServer().getRoomOperations("room-" + roomResponse.getId())
                            .sendEvent("room-players", Map.of(
                                    "roomId", roomResponse.getId(),
                                    "players", players
                            ));

                    client.sendEvent("join-room-success", Map.of(
                            "success", true,
                            "room", roomResponse,
                            "message", "Joined room successfully"));

                    log.info("User {} joined room {} via Socket.IO", userId, roomResponse.getId());
                    return;
                } catch (Exception roomJoinError) {
                    log.error("Error joining room via roomCode: {}", roomJoinError.getMessage());
                    
                    // If user already joined, treat as success
                    if (roomJoinError.getMessage().contains("already joined")) {
                        log.info("User {} already joined room, connecting to Socket.IO", userId);
                        try {
                            var roomResponse = handler.getRoomService().getRoomByCode(data.getRoomCode());
                            client.joinRoom("room-" + roomResponse.getId());
                            handler.getSessionManager().addRoomSession(client.getSessionId().toString(), roomResponse.getId());
                            
                            // Broadcast current players list
                            var players = handler.getRoomService().getRoomPlayers(roomResponse.getId());
                            handler.getSocketIOServer().getRoomOperations("room-" + roomResponse.getId())
                                    .sendEvent("room-players", Map.of(
                                            "roomId", roomResponse.getId(),
                                            "players", players
                                    ));
                            
                            client.sendEvent("join-room-success", Map.of(
                                    "success", true,
                                    "room", roomResponse,
                                    "message", "Connected to room successfully"));
                            return;
                        } catch (Exception getRoomError) {
                            log.error("Failed to get room info: {}", getRoomError.getMessage());
                        }
                    }
                    
                    throw roomJoinError; // Re-throw original error
                }
            }

            throw new IllegalArgumentException("No roomId or roomCode provided");

        } catch (Exception e) {
            log.error("Error in handleJoinRoom: {}", e.getMessage());
            client.sendEvent("join-room-error", Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    public void handleLeaveRoom(SocketIOClient client, LeaveRoomData data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null) {
            log.error("❌ Leave room failed: userId is null");
            return;
        }

        log.info("🚪 User {} attempting to leave room {}", userId, data.getRoomId());

        try {
            // Leave room in database
            log.info("📤 Calling roomService.leaveRoom for user {} in room {}", userId, data.getRoomId());
            handler.getRoomService().leaveRoom(data.getRoomId(), userId);
            log.info("✅ Successfully left room in database");

            // Leave Socket.IO room
            client.leaveRoom("room-" + data.getRoomId());
            handler.getSessionManager().removeRoomSession(client.getSessionId().toString());
            log.info("✅ Left Socket.IO room and removed session");

            String username = handler.getUserRepository().findById(userId)
                    .map(user -> user.getUsername()).orElse("Unknown");

            log.info("📡 Broadcasting player-left event for user {} ({})", userId, username);
            handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("player-left", Map.of(
                            "roomId", data.getRoomId(),
                            "userId", userId,
                            "username", username
                    ));

            // broadcast updated players list
            var players = handler.getRoomService().getRoomPlayers(data.getRoomId());
            log.info("📡 Broadcasting room-players event with {} players", players.size());
            handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("room-players", Map.of(
                            "roomId", data.getRoomId(),
                            "players", players
                    ));

            // Send success response to the client who left
            log.info("✅ Sending leave-room-success to client");
            client.sendEvent("leave-room-success", Map.of(
                    "success", true,
                    "roomId", data.getRoomId(),
                    "message", "Left room successfully"));

            log.info("🎉 User {} successfully left room {}", userId, data.getRoomId());

        } catch (Exception e) {
            log.error("❌ Error leaving room {}: {}", data.getRoomId(), e.getMessage(), e);
            client.sendEvent("leave-room-error", Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    public void handleKickPlayer(SocketIOClient client, KickPlayerData data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null)
            return;

        try {
            KickPlayerRequest request = new KickPlayerRequest();
            request.setPlayerId(data.getPlayerId());
            request.setReason(data.getReason());

            handler.getRoomService().kickPlayer(data.getRoomId(), request, userId);

            String kickedUsername = handler.getUserRepository().findById(data.getPlayerId())
                    .map(user -> user.getUsername()).orElse("Unknown");

            handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("player-kicked", Map.of(
                            "roomId", data.getRoomId(),
                            "playerId", data.getPlayerId(),
                            "reason", data.getReason(),
                            "kickedBy", userId,
                            "username", kickedUsername
                    ));

            // broadcast updated players list
            var players = handler.getRoomService().getRoomPlayers(data.getRoomId());
            handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("room-players", Map.of(
                            "roomId", data.getRoomId(),
                            "players", players
                    ));

            log.info("User {} kicked player {} from room {}", userId, data.getPlayerId(), data.getRoomId());

        } catch (Exception e) {
            client.sendEvent("kick-player-error", Map.of("message", e.getMessage()));
        }
    }

    public void handleStartGame(SocketIOClient client, StartGameData data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null)
            return;

        try {
            handler.getRoomService().startGame(data.getRoomId(), userId);
            handler.getGameService().startGameSession(data.getRoomId());

            // Build minimal game state
            var room = handler.getRoomService().getRoomById(data.getRoomId());
            var gameState = Map.of(
                    "roomId", data.getRoomId(),
                    "gameStatus", "PLAYING",
                    "totalQuestions", room.getQuestionCount(),
                    "currentQuestionNumber", 0,
                    "timeRemaining", handler.getGameService().getRemainingTime(data.getRoomId()),
                    "isHost", true
            );

            // Broadcast game started event to all players
            handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("game-started", Map.of(
                            "roomId", data.getRoomId(),
                            "gameState", gameState));

            // Get and send first question immediately
            try {
                var firstQuestion = handler.getGameService().getNextQuestion(data.getRoomId());
                if (firstQuestion != null) {
                    handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                        .sendEvent("next-question", Map.of(
                            "roomId", data.getRoomId(),
                            "questionId", firstQuestion.getQuestionId(),
                            "questionText", firstQuestion.getQuestionText(),
                            "answers", firstQuestion.getAnswers().stream().map(a -> Map.of(
                                "id", a.getId(),
                                "text", a.getText()
                            )).collect(java.util.stream.Collectors.toList()),
                            "imageUrl", null,
                            "timeLimit", firstQuestion.getTimeLimit(),
                            "currentQuestionNumber", firstQuestion.getQuestionNumber(),
                            "totalQuestions", firstQuestion.getTotalQuestions()
                        ));
                }
            } catch (Exception questionError) {
                log.error("Failed to get first question: {}", questionError.getMessage());
            }

            // Send success response to the client who started the game
            client.sendEvent("start-game-success", Map.of(
                    "success", true,
                    "roomId", data.getRoomId(),
                    "message", "Game started successfully"));

            log.info("User {} started game in room {}", userId, data.getRoomId());

        } catch (Exception e) {
            client.sendEvent("start-game-error", Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    public void handleGetPlayers(SocketIOClient client, GetPlayersData data, SocketIOEventHandler handler) {
        Long userId = getUserId(client, handler);
        if (userId == null)
            return;

        try {
            var players = handler.getRoomService().getRoomPlayers(data.getRoomId());

            client.sendEvent("room-players", Map.of(
                    "roomId", data.getRoomId(),
                    "players", players));

        } catch (Exception e) {
            client.sendEvent("get-players-error", Map.of("message", e.getMessage()));
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
