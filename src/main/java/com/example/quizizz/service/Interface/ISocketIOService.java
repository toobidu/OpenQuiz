package com.example.quizizz.service.Interface;

import java.util.Map;

public interface ISocketIOService {
    
    void sendToRoom(String roomId, String event, Object data);
    
    void sendToUser(String sessionId, String event, Object data);
    
    void broadcastToAll(String event, Object data);
    
    void notifyRoomUpdate(Long roomId, String eventType, Map<String, Object> data);
    
    void notifyGameEvent(Long roomId, String eventType, Map<String, Object> data);
    
    void kickUserFromRoom(String sessionId, Long roomId, String reason);
}