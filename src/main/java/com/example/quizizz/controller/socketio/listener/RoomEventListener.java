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
                client.joinRoom("room-" + data.getRoomId());
                handler.getSessionManager().addRoomSession(client.getSessionId().toString(), data.getRoomId());

                // Broadcast player-joined like in roomCode path so host UI updates in real-time
                String username = handler.getUserRepository().findById(userId)
                        .map(user -> user.getUsername()).orElse("Unknown");
                handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                        .sendEvent("player-joined", Map.of(
                                "roomId", data.getRoomId(),
                                "userId", userId,
                                "username", username
                        ));

                // broadcast updated players list
                var players = handler.getRoomService().getRoomPlayers(data.getRoomId());
                handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                        .sendEvent("room-players", Map.of(
                                "roomId", data.getRoomId(),
                                "players", players
                        ));

                client.sendEvent("join-room-success", Map.of(
                        "success", true,
                        "roomId", data.getRoomId(),
                        "message", "Joined Socket.IO room successfully"));

                log.info("User {} joined Socket.IO room {}", userId, data.getRoomId());
                return;
            }

            if (data.getRoomCode() != null) {
                JoinRoomRequest request = new JoinRoomRequest();
                request.setRoomCode(data.getRoomCode());

                var roomResponse = handler.getRoomService().joinRoom(request, userId);

                client.joinRoom("room-" + roomResponse.getId());
                handler.getSessionManager().addRoomSession(client.getSessionId().toString(), roomResponse.getId());

                String username = handler.getUserRepository().findById(userId)
                        .map(user -> user.getUsername()).orElse("Unknown");

                handler.getSocketIOServer().getRoomOperations("room-" + roomResponse.getId())
                        .sendEvent("player-joined", Map.of(
                                "roomId", roomResponse.getId(),
                                "userId", userId,
                                "username", username
                        ));

                // broadcast updated players list
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
        if (userId == null)
            return;

        try {
            handler.getRoomService().leaveRoom(data.getRoomId(), userId);

            client.leaveRoom("room-" + data.getRoomId());
            handler.getSessionManager().removeRoomSession(client.getSessionId().toString());

            handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("player-left", Map.of(
                            "roomId", data.getRoomId(),
                            "userId", userId
                    ));

            // broadcast updated players list
            var players = handler.getRoomService().getRoomPlayers(data.getRoomId());
            handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("room-players", Map.of(
                            "roomId", data.getRoomId(),
                            "players", players
                    ));

            log.info("User {} left room {}", userId, data.getRoomId());

        } catch (Exception e) {
            client.sendEvent("leave-room-error", Map.of("message", e.getMessage()));
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

            handler.getSocketIOServer().getRoomOperations("room-" + data.getRoomId())
                    .sendEvent("game-started", Map.of(
                            "roomId", data.getRoomId(),
                            "gameState", gameState));

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
