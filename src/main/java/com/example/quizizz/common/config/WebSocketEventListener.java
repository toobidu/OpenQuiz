package com.example.quizizz.common.config;

import com.example.quizizz.security.JwtUtil;
import com.example.quizizz.service.Interface.IRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final JwtUtil jwtUtil;
    private final IRoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    
    // Thread-safe storage for session -> room mapping
    private final Map<String, Long> sessionRoomMap = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        try {
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            if (token != null) {
                String username = jwtUtil.getUsernameFromToken(token);
                Long userId = Long.parseLong(username);
                sessionUserMap.put(sessionId, userId);
                log.info("WebSocket connected: sessionId={}, userId={}", sessionId, userId);
            }
        } catch (Exception e) {
            log.error("Error handling WebSocket connect: ", e);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        try {
            Long userId = sessionUserMap.get(sessionId);
            Long roomId = sessionRoomMap.get(sessionId);
            
            if (userId != null && roomId != null) {
                log.info("WebSocket disconnected: sessionId={}, userId={}, roomId={}", sessionId, userId, roomId);
                
                // Remove user from room
                roomService.leaveRoom(roomId, userId);
                
                // Broadcast LEAVE_ROOM event
                Map<String, Object> leaveEvent = new HashMap<>();
                leaveEvent.put("event", "LEAVE_ROOM");
                leaveEvent.put("data", Map.of("userId", userId));
                leaveEvent.put("timestamp", LocalDateTime.now());
                
                messagingTemplate.convertAndSend("/topic/room/" + roomId, leaveEvent);
                
                // Clean up mappings
                sessionRoomMap.remove(sessionId);
            }
            
            sessionUserMap.remove(sessionId);
            
        } catch (Exception e) {
            log.error("Error handling WebSocket disconnect: ", e);
        }
    }
    
    // Helper methods for session management
    public void addUserToRoom(String sessionId, Long roomId) {
        sessionRoomMap.put(sessionId, roomId);
    }
    
    public void removeUserFromRoom(String sessionId) {
        sessionRoomMap.remove(sessionId);
    }
}