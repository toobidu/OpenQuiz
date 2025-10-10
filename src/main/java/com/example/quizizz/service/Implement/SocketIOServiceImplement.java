package com.example.quizizz.service.Implement;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.quizizz.service.Interface.ISocketIOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketIOServiceImplement implements ISocketIOService {

    private final SocketIOServer socketIOServer;

    @Override
    public void sendToRoom(String roomId, String event, Object data) {
        try {
            socketIOServer.getRoomOperations("room-" + roomId).sendEvent(event, data);
            log.debug("Sent event '{}' to room '{}'", event, roomId);
        } catch (Exception e) {
            log.error("Error sending event '{}' to room '{}': {}", event, roomId, e.getMessage());
        }
    }

    @Override
    public void sendToUser(String sessionId, String event, Object data) {
        try {
            var client = socketIOServer.getClient(java.util.UUID.fromString(sessionId));
            if (client != null) {
                client.sendEvent(event, data);
                log.debug("Sent event '{}' to user session '{}'", event, sessionId);
            }
        } catch (Exception e) {
            log.error("Error sending event '{}' to user '{}': {}", event, sessionId, e.getMessage());
        }
    }

    @Override
    public void broadcastToAll(String event, Object data) {
        try {
            socketIOServer.getBroadcastOperations().sendEvent(event, data);
            log.debug("Broadcasted event '{}' to all users", event);
        } catch (Exception e) {
            log.error("Error broadcasting event '{}': {}", event, e.getMessage());
        }
    }

    @Override
    public void notifyRoomUpdate(Long roomId, String eventType, Map<String, Object> data) {
        Map<String, Object> payload = Map.of(
                "type", eventType,
                "roomId", roomId,
                "data", data,
                "timestamp", System.currentTimeMillis()
        );
        sendToRoom(roomId.toString(), "room-update", payload);
    }

    @Override
    public void notifyGameEvent(Long roomId, String eventType, Map<String, Object> data) {
        Map<String, Object> payload = Map.of(
                "type", eventType,
                "roomId", roomId,
                "data", data,
                "timestamp", System.currentTimeMillis()
        );
        sendToRoom(roomId.toString(), "game-event", payload);
    }

    @Override
    public void kickUserFromRoom(String sessionId, Long roomId, String reason) {
        try {
            var client = socketIOServer.getClient(java.util.UUID.fromString(sessionId));
            if (client != null) {
                // Send kick notification to user
                client.sendEvent("kicked-from-room", Map.of(
                        "roomId", roomId,
                        "reason", reason,
                        "timestamp", System.currentTimeMillis()
                ));

                // Remove from room
                client.leaveRoom("room-" + roomId);

                log.info("Kicked user session '{}' from room '{}' with reason: {}", sessionId, roomId, reason);
            }
        } catch (Exception e) {
            log.error("Error kicking user '{}' from room '{}': {}", sessionId, roomId, e.getMessage());
        }
    }
}