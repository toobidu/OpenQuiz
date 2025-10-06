package com.example.quizizz.controller.socketio.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.quizizz.controller.socketio.SocketIOEventHandler;
import com.example.quizizz.controller.socketio.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventRegistrar {

    private final RoomEventListener roomEventListener;
    private final GameEventListener gameEventListener;

    public void registerEvents(SocketIOServer server, SocketIOEventHandler handler) {
        // Debug and test events
        server.addEventListener("ping", Object.class,
                (client, data, ackSender) -> {
                    log.info("🏓 Received ping from client: {}", client.getSessionId());
                    ackSender.sendAckData("pong", System.currentTimeMillis());
                });

        server.addEventListener("test-connection", Object.class,
                (client, data, ackSender) -> {
                    log.info("🧪 Received test-connection from client: {}", client.getSessionId());
                    client.sendEvent("test-response", "Backend received your test");
                });

        server.addEventListener("subscribe-room-list", Object.class,
                (client, data, ackSender) -> {
                    log.info("📋 Client {} subscribed to room list", client.getSessionId());
                    client.joinRoom("room-list");  // Join room riêng
                    client.sendEvent("subscription-confirmed", "You are now subscribed to room updates");
                });

        server.addEventListener("unsubscribe-room-list", Object.class,
                (client, data, ackSender) -> {
                    client.leaveRoom("room-list");
                    log.info("Client {} unsubscribed from room list", client.getSessionId());
                });

        // Room events
        server.addEventListener("join-room", JoinRoomData.class,
                (client, data, ackSender) -> roomEventListener.handleJoinRoom(client, data, handler));
        server.addEventListener("leave-room", LeaveRoomData.class,
                (client, data, ackSender) -> roomEventListener.handleLeaveRoom(client, data, handler));
        server.addEventListener("kick-player", KickPlayerData.class,
                (client, data, ackSender) -> roomEventListener.handleKickPlayer(client, data, handler));
        server.addEventListener("start-game", StartGameData.class,
                (client, data, ackSender) -> roomEventListener.handleStartGame(client, data, handler));
        server.addEventListener("get-players", GetPlayersData.class,
                (client, data, ackSender) -> roomEventListener.handleGetPlayers(client, data, handler));

        // Game events
        // Legacy event for backward compatibility
        server.addEventListener("submit-answer", SubmitAnswerData.class,
                (client, data, ackSender) -> gameEventListener.handleSubmitAnswer(client, data, handler));
        // Spec-compliant event (camelCase)
        server.addEventListener("submitAnswer", SubmitAnswerFrontDto.class,
                (client, data, ackSender) -> gameEventListener.handleSubmitAnswerFront(client, data, handler));
        server.addEventListener("getGameState", GetGameStateData.class,
                (client, data, ackSender) -> gameEventListener.handleGetGameState(client, data, handler));
        server.addEventListener("next-question", NextQuestionData.class,
                (client, data, ackSender) -> gameEventListener.handleNextQuestion(client, data, handler));

        log.info("Socket.IO events registered successfully");
    }
}