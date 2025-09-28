package com.example.quizizz.service.Implement;

import com.example.quizizz.common.constants.MessageCode;
import com.example.quizizz.common.constants.RoomMode;
import com.example.quizizz.common.constants.RoomStatus;
import com.example.quizizz.common.exception.ApiException;
import com.example.quizizz.mapper.RoomMapper;
import com.example.quizizz.model.dto.room.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.quizizz.model.entity.*;
import com.example.quizizz.repository.*;
import com.example.quizizz.service.Interface.IRoomService;

import com.example.quizizz.util.RoomCodeGenerator;
import com.example.quizizz.mapper.RoomPlayerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImplement implements IRoomService {

    private final RoomRepository roomRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final UserRepository userRepository;

    private final RoomMapper roomMapper;
    private final RoomPlayerMapper roomPlayerMapper;
    private final RoomCodeGenerator roomCodeGenerator;

    @Override
    public RoomResponse createRoom(CreateRoomRequest request, Long userId) {
        validateRoomModeAndMaxPlayers(request.getRoomMode(), request.getMaxPlayers());

        Room room = roomMapper.toEntity(request);
        room.setOwnerId(userId);
        room.setStatus(RoomStatus.WAITING.name());
        room.setRoomCode(generateUniqueRoomCode());

        if (request.getMaxPlayers() == null) {
            room.setMaxPlayers(request.getRoomMode() == RoomMode.ONE_VS_ONE ? 2 : 10);
        }

        Room savedRoom = roomRepository.save(room);

        RoomPlayers hostPlayer = new RoomPlayers();
        hostPlayer.setRoomId(savedRoom.getId());
        hostPlayer.setUserId(userId);
        hostPlayer.setIsHost(true);
        hostPlayer.setJoinOrder(1);
        hostPlayer.setStatus("ACTIVE");
        roomPlayerRepository.save(hostPlayer);

        return roomMapper.toResponse(savedRoom);
    }

    @Override
    public RoomResponse joinRoom(JoinRoomRequest request, Long userId) {
        Room room = roomRepository.findByRoomCode(request.getRoomCode())
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));

        if (!room.getStatus().equals(RoomStatus.WAITING.name())) {
            throw new ApiException(MessageCode.ROOM_ALREADY_STARTED);
        }

        Integer currentPlayers = roomPlayerRepository.countPlayersInRoom(room.getId());
        if (currentPlayers >= room.getMaxPlayers()) {
            throw new ApiException(MessageCode.ROOM_FULL);
        }

        if (roomPlayerRepository.existsByRoomIdAndUserId(room.getId(), userId)) {
            throw new ApiException(MessageCode.ROOM_ALREADY_JOINED);
        }

        if (roomPlayerRepository.isUserKicked(room.getId(), userId)) {
            throw new ApiException(MessageCode.ROOM_PERMISSION_DENIED);
        }

        Integer nextJoinOrder = roomPlayerRepository.getMaxJoinOrderInRoom(room.getId()) + 1;

        RoomPlayers player = new RoomPlayers();
        player.setRoomId(room.getId());
        player.setUserId(userId);
        player.setIsHost(false);
        player.setJoinOrder(nextJoinOrder);
        player.setStatus("ACTIVE");
        roomPlayerRepository.save(player);

        return roomMapper.toResponse(room);
    }

    @Override
    public void leaveRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));

        if (!roomPlayerRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new ApiException(MessageCode.ROOM_NOT_JOINED);
        }

        if (room.getOwnerId().equals(userId)) {
            handleHostLeaving(room);
        } else {
            roomPlayerRepository.deleteByRoomIdAndUserId(roomId, userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomPlayerResponse> getRoomPlayers(Long roomId) {
        roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));

        List<RoomPlayers> players = roomPlayerRepository.findByRoomIdOrderByJoinOrder(roomId);
        return players.stream()
                .map(player -> {
                    User user = userRepository.findById(player.getUserId())
                            .orElse(null);
                    return user != null ? 
                            roomPlayerMapper.toResponse(player, user) : 
                            roomPlayerMapper.toResponse(player);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void kickPlayer(Long roomId, KickPlayerRequest request, Long hostId) {
        roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));

        if (!isRoomHost(roomId, hostId)) {
            throw new ApiException(MessageCode.ROOM_PERMISSION_DENIED);
        }

        if (hostId.equals(request.getPlayerId())) {
            throw new ApiException(MessageCode.BAD_REQUEST);
        }

        if (!roomPlayerRepository.existsByRoomIdAndUserId(roomId, request.getPlayerId())) {
            throw new ApiException(MessageCode.PLAYER_NOT_IN_GAME);
        }

        RoomPlayers player = roomPlayerRepository.findByRoomIdAndUserId(roomId, request.getPlayerId()).get();
        player.setStatus("KICKED");
        roomPlayerRepository.save(player);
    }

    @Override
    public InvitationResponse invitePlayer(InvitePlayerRequest request, Long inviterId) {
        // Simplified - just return null for now, implement with Redis later
        return null;
    }

    @Override
    public RoomResponse respondToInvitation(Long invitationId, boolean accept, Long userId) {
        // TODO: Implement invitation logic later
        throw new ApiException(MessageCode.NOT_IMPLEMENTED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitationResponse> getUserInvitations(Long userId) {
        // TODO: Implement invitation logic later
        return List.of();
    }

    private void handleHostLeaving(Room room) {
        List<RoomPlayers> players = roomPlayerRepository.findByRoomIdOrderByJoinOrder(room.getId());
        roomPlayerRepository.deleteByRoomIdAndUserId(room.getId(), room.getOwnerId());

        if (players.size() <= 1) {
            handleEmptyRoom(room);
        } else {
            RoomPlayers nextHost = players.stream()
                    .filter(p -> !p.getUserId().equals(room.getOwnerId()))
                    .min((p1, p2) -> p1.getJoinOrder().compareTo(p2.getJoinOrder()))
                    .orElseThrow(() -> new ApiException(MessageCode.PLAYER_NOT_IN_GAME));

            room.setOwnerId(nextHost.getUserId());
            roomRepository.save(room);

            nextHost.setIsHost(true);
            roomPlayerRepository.save(nextHost);
        }
    }

    /**
     * Xử lý khi phòng trống
     * Case 1: Chưa có game history -> Xóa phòng
     * Case 2: Đã có game history -> Archive phòng
     */
    private void handleEmptyRoom(Room room) {
        if (!room.getHasGameHistory()) {
            // Case 1: Phòng chưa chơi game -> Xóa hoàn toàn
            roomPlayerRepository.deleteByRoomId(room.getId());
            roomRepository.delete(room);
        } else {
            // Case 2: Phòng đã có lịch sử game -> Archive
            room.setStatus(RoomStatus.ARCHIVED.name());
            roomRepository.save(room);
            // Giữ lại room và xóa players
            roomPlayerRepository.deleteByRoomId(room.getId());
        }
    }

    @Override
    public RoomResponse updateRoom(Long roomId, UpdateRoomRequest request, Long userId) {
        return null;
    }

    @Override
    public void deleteRoom(Long roomId, Long userId) {
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomByCode(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByOwner(Long ownerId) {
        List<Room> rooms = roomRepository.findByOwnerId(ownerId);
        return rooms.stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getPublicRoomsByStatus(RoomStatus status) {
        List<Room> rooms = roomRepository.findPublicRoomsByStatus(status);
        return rooms.stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> searchPublicRooms(String roomName) {
        List<Room> rooms = roomRepository.findPublicRoomsByRoomNameContaining(roomName);
        return rooms.stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse transferHost(Long roomId, Long newHostId, Long currentHostId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));

        if (!isRoomHost(roomId, currentHostId)) {
            throw new ApiException(MessageCode.ROOM_PERMISSION_DENIED);
        }

        if (!isUserInRoom(roomId, newHostId)) {
            throw new ApiException(MessageCode.PLAYER_NOT_IN_GAME);
        }

        room.setOwnerId(newHostId);
        Room updatedRoom = roomRepository.save(room);

        RoomPlayers oldHost = roomPlayerRepository.findByRoomIdAndUserId(roomId, currentHostId).get();
        RoomPlayers newHost = roomPlayerRepository.findByRoomIdAndUserId(roomId, newHostId).get();
        
        oldHost.setIsHost(false);
        newHost.setIsHost(true);
        
        roomPlayerRepository.save(oldHost);
        roomPlayerRepository.save(newHost);

        return roomMapper.toResponse(updatedRoom);
    }

    @Override
    public void startGame(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));

        if (!isRoomHost(roomId, userId)) {
            throw new ApiException(MessageCode.ROOM_PERMISSION_DENIED);
        }

        if (!room.getStatus().equals(RoomStatus.WAITING.name())) {
            throw new ApiException(MessageCode.GAME_ALREADY_STARTED);
        }

        room.setStatus(RoomStatus.PLAYING.name());
        room.setHasGameHistory(true); // Đánh dấu phòng đã có lịch sử game
        roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRoomHost(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));
        return room.getOwnerId().equals(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserInRoom(Long roomId, Long userId) {
        return roomPlayerRepository.existsByRoomIdAndUserId(roomId, userId);
    }

    private void validateRoomModeAndMaxPlayers(RoomMode roomMode, Integer maxPlayers) {
        if (roomMode == RoomMode.ONE_VS_ONE && maxPlayers != null && maxPlayers != 2) {
            throw new ApiException(MessageCode.ROOM_INVALID_MAX_PLAYERS);
        }
        if (roomMode == RoomMode.BATTLE_ROYAL && maxPlayers != null && (maxPlayers < 3 || maxPlayers > 50)) {
            throw new ApiException(MessageCode.ROOM_INVALID_MAX_PLAYERS);
        }
    }

    @Override
    public PagedRoomResponse getPublicRoomsWithPagination(RoomStatus status, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Room> roomPage;
        
        if (search != null && !search.trim().isEmpty()) {
            roomPage = roomRepository.findPublicRoomsByStatusAndSearch(status, search.trim(), pageable);
        } else {
            roomPage = roomRepository.findPublicRoomsByStatusWithPagination(status, pageable);
        }
        
        return mapToPagedResponse(roomPage);
    }

    @Override
    public PagedRoomResponse getMyRoomsWithPagination(Long userId, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Room> roomPage;
        
        if (search != null && !search.trim().isEmpty()) {
            roomPage = roomRepository.findByOwnerIdAndSearch(userId, search.trim(), pageable);
        } else {
            roomPage = roomRepository.findByOwnerIdWithPagination(userId, pageable);
        }
        
        return mapToPagedResponse(roomPage);
    }

    private PagedRoomResponse mapToPagedResponse(Page<Room> roomPage) {
        PagedRoomResponse response = new PagedRoomResponse();
        response.setRooms(roomPage.getContent().stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList()));
        response.setCurrentPage(roomPage.getNumber());
        response.setTotalPages(roomPage.getTotalPages());
        response.setTotalElements(roomPage.getTotalElements());
        response.setPageSize(roomPage.getSize());
        response.setHasNext(roomPage.hasNext());
        response.setHasPrevious(roomPage.hasPrevious());
        return response;
    }

    private String generateUniqueRoomCode() {
        String roomCode;
        do {
            roomCode = roomCodeGenerator.generateRoomCode();
        } while (roomRepository.existsByRoomCode(roomCode));
        return roomCode;
    }
}
