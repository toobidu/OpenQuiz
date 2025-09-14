package com.example.quizizz.controller.core;

import com.example.quizizz.common.config.ApiResponse;
import com.example.quizizz.common.constants.MessageCode;
import com.example.quizizz.common.constants.RoomStatus;
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
 * Core Controller cho CRUD cơ bản của Room
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Core", description = "Basic CRUD operations for rooms")
public class RoomCoreController {

    private final IRoomService roomService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Tạo phòng mới")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        RoomResponse roomResponse = roomService.createRoom(request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROOM_CREATED, roomResponse));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "Lấy thông tin phòng theo ID")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long roomId) {
        RoomResponse roomResponse = roomService.getRoomById(roomId);
        return ResponseEntity.ok(ApiResponse.success(roomResponse));
    }

    @GetMapping("/code/{roomCode}")
    @Operation(summary = "Lấy thông tin phòng theo room code")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomByCode(@PathVariable String roomCode) {
        RoomResponse roomResponse = roomService.getRoomByCode(roomCode);
        return ResponseEntity.ok(ApiResponse.success(roomResponse));
    }

    @PutMapping("/{roomId}")
    @Operation(summary = "Cập nhật thông tin phòng")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody UpdateRoomRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        RoomResponse roomResponse = roomService.updateRoom(roomId, request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROOM_UPDATED, roomResponse));
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "Xóa phòng")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable Long roomId,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        roomService.deleteRoom(roomId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROOM_DELETED, null));
    }

    @GetMapping("/my-rooms")
    @Operation(summary = "Lấy danh sách phòng của user")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getMyRooms(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<RoomResponse> rooms = roomService.getRoomsByOwner(userId);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/public")
    @Operation(summary = "Lấy danh sách phòng public")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getPublicRooms(
            @RequestParam(required = false, defaultValue = "WAITING") RoomStatus status) {
        
        List<RoomResponse> rooms = roomService.getPublicRoomsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm phòng public")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> searchPublicRooms(@RequestParam String roomName) {
        List<RoomResponse> rooms = roomService.searchPublicRooms(roomName);
        return ResponseEntity.ok(ApiResponse.success(rooms));
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
