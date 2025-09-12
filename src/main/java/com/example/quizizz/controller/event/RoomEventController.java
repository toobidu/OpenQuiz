package com.example.quizizz.controller.event;

import com.example.quizizz.model.dto.room.*;
import com.example.quizizz.service.Interface.IRoomService;
import com.example.quizizz.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

/**
 * Event Controller cho các sự kiện WebSocket real-time
 * Chỉ xử lý các sự kiện socket, không có logic business phức tạp
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomEventController {

    private final IRoomService roomService;
    private final JwtUtil jwtUtil;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Xử lý sự kiện join phòng qua WebSocket
     * Client gửi: /app/room/{roomId}/join
     * Broadcast đến: /topic/room/{roomId}
     */
    @MessageMapping("/room/{roomId}/join")
    @SendTo("/topic/room/{roomId}")
    public Map<String, Object> handleJoinRoom(
            @DestinationVariable Long roomId,
            @Payload JoinRoomRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            /** Lấy user ID từ JWT token */
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = Long.parseLong(username); // Assuming username is userId for simplicity
            
            log.info("WebSocket: User {} joining room {}", userId, roomId);
            
            /** Gọi service để join phòng */
            RoomResponse roomResponse = roomService.joinRoom(request, userId);
            
            /** Tạo response event */
            Map<String, Object> response = new HashMap<>();
            response.put("type", "PLAYER_JOINED");
            response.put("roomId", roomId);
            response.put("playerId", userId);
            response.put("room", roomResponse);
            response.put("timestamp", LocalDateTime.now());
            response.put("success", true);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error joining room via WebSocket: ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "JOIN_ERROR");
            errorResponse.put("roomId", roomId);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("success", false);
            
            return errorResponse;
        }
    }

    /**
     * Xử lý sự kiện leave phòng qua WebSocket
     * Client gửi: /app/room/{roomId}/leave
     * Broadcast đến: /topic/room/{roomId}
     */
    @MessageMapping("/room/{roomId}/leave")
    @SendTo("/topic/room/{roomId}")
    public Map<String, Object> handleLeaveRoom(
            @DestinationVariable Long roomId,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            /** Lấy user ID từ JWT token */
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = Long.parseLong(username);
            
            log.info("WebSocket: User {} leaving room {}", userId, roomId);
            
            /** Gọi service để leave phòng */
            roomService.leaveRoom(roomId, userId);
            
            /** Tạo response event */
            Map<String, Object> response = new HashMap<>();
            response.put("type", "PLAYER_LEFT");
            response.put("roomId", roomId);
            response.put("playerId", userId);
            response.put("timestamp", LocalDateTime.now());
            response.put("success", true);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error leaving room via WebSocket: ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "LEAVE_ERROR");
            errorResponse.put("roomId", roomId);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("success", false);
            
            return errorResponse;
        }
    }

    /**
     * Xử lý sự kiện kick player qua WebSocket
     * Client gửi: /app/room/{roomId}/kick
     * Broadcast đến: /topic/room/{roomId}
     */
    @MessageMapping("/room/{roomId}/kick")
    @SendTo("/topic/room/{roomId}")
    public Map<String, Object> handleKickPlayer(
            @DestinationVariable Long roomId,
            @Payload KickPlayerRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            /** Lấy host ID từ JWT token */
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            Long hostId = Long.parseLong(username);
            
            log.info("WebSocket: Host {} kicking player {} from room {}", 
                    hostId, request.getPlayerId(), roomId);
            
            /** Gọi service để kick player */
            roomService.kickPlayer(roomId, request, hostId);
            
            /** Tạo response event */
            Map<String, Object> response = new HashMap<>();
            response.put("type", "PLAYER_KICKED");
            response.put("roomId", roomId);
            response.put("kickedPlayerId", request.getPlayerId());
            response.put("kickedBy", hostId);
            response.put("reason", request.getReason());
            response.put("timestamp", LocalDateTime.now());
            response.put("success", true);
            
            /** Gửi thông báo riêng cho player bị kick */
            Map<String, Object> kickNotification = new HashMap<>();
            kickNotification.put("type", "YOU_WERE_KICKED");
            kickNotification.put("roomId", roomId);
            kickNotification.put("kickedBy", hostId);
            kickNotification.put("reason", request.getReason());
            kickNotification.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSendToUser(
                    request.getPlayerId().toString(),
                    "/queue/notifications",
                    kickNotification
            );
            
            return response;
            
        } catch (Exception e) {
            log.error("Error kicking player via WebSocket: ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "KICK_ERROR");
            errorResponse.put("roomId", roomId);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("success", false);
            
            return errorResponse;
        }
    }

    /**
     * Xử lý sự kiện mời player qua WebSocket
     * Client gửi: /app/room/invite
     * Gửi trực tiếp đến user được mời: /user/{username}/queue/invitations
     */
    @MessageMapping("/room/invite")
    public void handleInvitePlayer(
            @Payload InvitePlayerRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            /** Lấy inviter ID từ JWT token */
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            Long inviterId = Long.parseLong(username);
            
            log.info("WebSocket: User {} inviting {} to room {}", 
                    inviterId, request.getUsername(), request.getRoomId());
            
            /** Gọi service để tạo lời mời */
            InvitationResponse invitation = roomService.invitePlayer(request, inviterId);
            
            /** Tạo notification cho người được mời */
            Map<String, Object> invitationNotification = new HashMap<>();
            invitationNotification.put("type", "ROOM_INVITATION");
            invitationNotification.put("invitation", invitation);
            invitationNotification.put("timestamp", LocalDateTime.now());
            
            /** Gửi notification đến user được mời */
            messagingTemplate.convertAndSendToUser(
                    request.getUsername(),
                    "/queue/invitations",
                    invitationNotification
            );
            
            /** Gửi confirmation cho người mời */
            Map<String, Object> confirmation = new HashMap<>();
            confirmation.put("type", "INVITATION_SENT");
            confirmation.put("invitedUsername", request.getUsername());
            confirmation.put("roomId", request.getRoomId());
            confirmation.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSendToUser(
                    inviterId.toString(),
                    "/queue/notifications",
                    confirmation
            );
            
        } catch (Exception e) {
            log.error("Error inviting player via WebSocket: ", e);
            
            /** Gửi error notification cho người mời */
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            
            Map<String, Object> errorNotification = new HashMap<>();
            errorNotification.put("type", "INVITATION_ERROR");
            errorNotification.put("error", e.getMessage());
            errorNotification.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/notifications",
                    errorNotification
            );
        }
    }

    /**
     * Xử lý sự kiện transfer host qua WebSocket
     * Client gửi: /app/room/{roomId}/transfer-host
     * Broadcast đến: /topic/room/{roomId}
     */
    @MessageMapping("/room/{roomId}/transfer-host")
    @SendTo("/topic/room/{roomId}")
    public Map<String, Object> handleTransferHost(
            @DestinationVariable Long roomId,
            @Payload Map<String, Long> payload,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            /** Lấy current host ID từ JWT token */
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            Long currentHostId = Long.parseLong(username);
            
            Long newHostId = payload.get("newHostId");
            
            log.info("WebSocket: Transferring host from {} to {} in room {}", 
                    currentHostId, newHostId, roomId);
            
            /** Gọi service để transfer host */
            RoomResponse roomResponse = roomService.transferHost(roomId, newHostId, currentHostId);
            
            /** Tạo response event */
            Map<String, Object> response = new HashMap<>();
            response.put("type", "HOST_TRANSFERRED");
            response.put("roomId", roomId);
            response.put("oldHostId", currentHostId);
            response.put("newHostId", newHostId);
            response.put("room", roomResponse);
            response.put("timestamp", LocalDateTime.now());
            response.put("success", true);
            
            /** Gửi notification riêng cho host mới */
            Map<String, Object> hostNotification = new HashMap<>();
            hostNotification.put("type", "YOU_ARE_NOW_HOST");
            hostNotification.put("roomId", roomId);
            hostNotification.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSendToUser(
                    newHostId.toString(),
                    "/queue/notifications",
                    hostNotification
            );
            
            return response;
            
        } catch (Exception e) {
            log.error("Error transferring host via WebSocket: ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "TRANSFER_HOST_ERROR");
            errorResponse.put("roomId", roomId);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("success", false);
            
            return errorResponse;
        }
    }

    /**
     * Xử lý sự kiện start game qua WebSocket
     * Client gửi: /app/room/{roomId}/start-game
     * Broadcast đến: /topic/room/{roomId}
     */
    @MessageMapping("/room/{roomId}/start-game")
    @SendTo("/topic/room/{roomId}")
    public Map<String, Object> handleStartGame(
            @DestinationVariable Long roomId,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            /** Lấy host ID từ JWT token */
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            Long hostId = Long.parseLong(username);
            
            log.info("WebSocket: Host {} starting game in room {}", hostId, roomId);
            
            /** Gọi service để start game */
            roomService.startGame(roomId, hostId);
            
            /** Tạo response event */
            Map<String, Object> response = new HashMap<>();
            response.put("type", "GAME_STARTED");
            response.put("roomId", roomId);
            response.put("startedBy", hostId);
            response.put("timestamp", LocalDateTime.now());
            response.put("success", true);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error starting game via WebSocket: ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "START_GAME_ERROR");
            errorResponse.put("roomId", roomId);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("success", false);
            
            return errorResponse;
        }
    }

    /**
     * Xử lý sự kiện lấy danh sách players trong phòng
     * Client gửi: /app/room/{roomId}/players
     * Gửi trực tiếp cho client: /user/{username}/queue/room-players
     */
    @MessageMapping("/room/{roomId}/players")
    public void handleGetRoomPlayers(
            @DestinationVariable Long roomId,
            SimpMessageHeaderAccessor headerAccessor) {
        
        try {
            /** Lấy user ID từ JWT token */
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            
            log.info("WebSocket: User {} requesting players list for room {}", username, roomId);
            
            /** Lấy danh sách players */
            var players = roomService.getRoomPlayers(roomId);
            
            /** Tạo response */
            Map<String, Object> response = new HashMap<>();
            response.put("type", "ROOM_PLAYERS");
            response.put("roomId", roomId);
            response.put("players", players);
            response.put("timestamp", LocalDateTime.now());
            
            /** Gửi trực tiếp cho client yêu cầu */
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/room-players",
                    response
            );
            
        } catch (Exception e) {
            log.error("Error getting room players via WebSocket: ", e);
            
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("type", "GET_PLAYERS_ERROR");
            errorResponse.put("roomId", roomId);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/room-players",
                    errorResponse
            );
        }
    }
}