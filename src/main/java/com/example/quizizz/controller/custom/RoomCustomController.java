package com.example.quizizz.controller.custom;

import com.example.quizizz.common.config.ApiResponse;
import com.example.quizizz.common.constants.MessageCode;
import com.example.quizizz.model.dto.room.*;
import com.example.quizizz.service.Interface.IRoomService;
import com.example.quizizz.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Custom Controller cho các API phức tạp kết hợp nhiều logic
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/rooms/custom")
@RequiredArgsConstructor
@Tag(name = "Room Custom", description = "Complex room operations")
public class RoomCustomController {

    private final IRoomService roomService;
    private final JwtUtil jwtUtil;

    @PostMapping("/join")
    @Operation(summary = "Join phòng bằng room code")
    public ResponseEntity<ApiResponse<RoomResponse>> joinRoom(
            @Valid @RequestBody JoinRoomRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        RoomResponse roomResponse = roomService.joinRoom(request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROOM_JOINED, roomResponse));
    }

    @DeleteMapping("/{roomId}/leave")
    @Operation(summary = "Rời khỏi phòng")
    public ResponseEntity<ApiResponse<Void>> leaveRoom(
            @PathVariable Long roomId,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        roomService.leaveRoom(roomId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROOM_LEFT, null));
    }

    @GetMapping("/{roomId}/players")
    @Operation(summary = "Lấy danh sách người chơi trong phòng")
    public ResponseEntity<ApiResponse<List<RoomPlayerResponse>>> getRoomPlayers(@PathVariable Long roomId) {
        List<RoomPlayerResponse> players = roomService.getRoomPlayers(roomId);
        return ResponseEntity.ok(ApiResponse.success(players));
    }

    @DeleteMapping("/{roomId}/kick")
    @Operation(summary = "Kick người chơi khỏi phòng")
    public ResponseEntity<ApiResponse<Void>> kickPlayer(
            @PathVariable Long roomId,
            @Valid @RequestBody KickPlayerRequest request,
            HttpServletRequest httpRequest) {
        
        Long hostId = getUserIdFromRequest(httpRequest);
        roomService.kickPlayer(roomId, request, hostId);
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.PLAYER_LEFT_GAME, null));
    }

    @PostMapping("/invite")
    @Operation(summary = "Mời người chơi vào phòng")
    public ResponseEntity<ApiResponse<InvitationResponse>> invitePlayer(
            @Valid @RequestBody InvitePlayerRequest request,
            HttpServletRequest httpRequest) {
        
        Long inviterId = getUserIdFromRequest(httpRequest);
        InvitationResponse invitation = roomService.invitePlayer(request, inviterId);
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, invitation));
    }

    @PostMapping("/invitations/{invitationId}/respond")
    @Operation(summary = "Phản hồi lời mời")
    public ResponseEntity<ApiResponse<RoomResponse>> respondToInvitation(
            @PathVariable Long invitationId,
            @RequestParam boolean accept,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        RoomResponse roomResponse = roomService.respondToInvitation(invitationId, accept, userId);
        
        if (accept && roomResponse != null) {
            return ResponseEntity.ok(ApiResponse.success(MessageCode.ROOM_JOINED, roomResponse));
        } else {
            return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, null));
        }
    }

    @GetMapping("/invitations")
    @Operation(summary = "Lấy danh sách lời mời của user")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getUserInvitations(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<InvitationResponse> invitations = roomService.getUserInvitations(userId);
        return ResponseEntity.ok(ApiResponse.success(invitations));
    }

    @PostMapping("/{roomId}/transfer-host")
    @Operation(summary = "Chuyển quyền host")
    public ResponseEntity<ApiResponse<RoomResponse>> transferHost(
            @PathVariable Long roomId,
            @RequestParam Long newHostId,
            HttpServletRequest httpRequest) {
        
        Long currentHostId = getUserIdFromRequest(httpRequest);
        RoomResponse roomResponse = roomService.transferHost(roomId, newHostId, currentHostId);
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROOM_HOST_TRANSFERRED, roomResponse));
    }

    @PostMapping("/{roomId}/start")
    @Operation(summary = "Bắt đầu game")
    public ResponseEntity<ApiResponse<Void>> startGame(
            @PathVariable Long roomId,
            HttpServletRequest httpRequest) {
        
        Long hostId = getUserIdFromRequest(httpRequest);
        roomService.startGame(roomId, hostId);
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.GAME_STARTED, null));
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("No valid JWT token found");
    }
}
