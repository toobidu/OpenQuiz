package com.example.quizizz.service.Implement;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.quizizz.common.constants.MessageCode;
import com.example.quizizz.common.constants.RoomMode;
import com.example.quizizz.common.constants.RoomStatus;
import com.example.quizizz.common.exception.ApiException;
import com.example.quizizz.mapper.RoomMapper;
import com.example.quizizz.mapper.RoomPlayerMapper;
import com.example.quizizz.model.dto.room.*;
import com.example.quizizz.model.entity.Room;
import com.example.quizizz.model.entity.RoomPlayers;
import com.example.quizizz.model.entity.User;
import com.example.quizizz.repository.RoomPlayerRepository;
import com.example.quizizz.repository.RoomRepository;
import com.example.quizizz.repository.TopicRepository;
import com.example.quizizz.repository.UserRepository;
import com.example.quizizz.service.Interface.IRoomService;
import com.example.quizizz.util.RoomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImplement implements IRoomService {

    private final RoomRepository roomRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final SocketIOServer socketIOServer;

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

        log.info("Room {} created successfully by user {}", savedRoom.getId(), userId);

        RoomResponse roomResponse = mapToRoomResponse(savedRoom);

        // 🔥 Broadcast roomCreated to all clients on RoomPage
        broadcastRoomCreated(roomResponse);

        return roomResponse;
    }

    @Override
    public RoomResponse joinRoom(JoinRoomRequest request, Long userId) {
        Room room = roomRepository.findByRoomCode(request.getRoomCode())
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));

        if (room.getStatus().equals(RoomStatus.PLAYING.name()) || room.getStatus().equals(RoomStatus.FINISHED.name())) {
            throw new ApiException(MessageCode.ROOM_ALREADY_STARTED);
        }

        if (room.getStatus().equals(RoomStatus.FULL.name())) {
            throw new ApiException(MessageCode.ROOM_FULL);
        }

        Integer currentPlayers = roomPlayerRepository.countPlayersInRoom(room.getId());
        if (currentPlayers >= room.getMaxPlayers()) {
            throw new ApiException(MessageCode.ROOM_FULL);
        }

        if (roomPlayerRepository.existsByRoomIdAndUserId(room.getId(), userId)) {
            // User already in room - return success with room data
            log.info("User {} already in room {}, returning room data", userId, room.getId());
            return mapToRoomResponse(room);
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

        // Cập nhật trạng thái phòng nếu đủ người
        updateRoomStatus(room);

        RoomResponse roomResponse = mapToRoomResponse(room);
        broadcastRoomUpdated(roomResponse);
        return roomResponse;
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
            updateRoomStatus(room);
        }

        Room roomAfterLeave = roomRepository.findById(roomId).orElse(null);
        if (roomAfterLeave != null) {
            broadcastRoomUpdated(mapToRoomResponse(roomAfterLeave));
        } else {
            broadcastRoomDeleted(roomId);
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
                    return user != null ? roomPlayerMapper.toResponse(player, user)
                            : roomPlayerMapper.toResponse(player);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void kickPlayer(Long roomId, KickPlayerRequest request, Long hostId) {
        Room room = roomRepository.findById(roomId)
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

        broadcastRoomUpdated(mapToRoomResponse(room));
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
            // Case 2: Phòng đã có lịch sử game -> Archive và xóa players
            room.setStatus(RoomStatus.ARCHIVED.name());
            roomRepository.save(room);
            roomPlayerRepository.deleteByRoomId(room.getId());
        }
    }

    @Override
    public RoomResponse updateRoom(Long roomId, UpdateRoomRequest request, Long userId) {
        return null;
    }

    @Override
    public void deleteRoom(Long roomId, Long userId) {
        // Kiểm tra phòng tồn tại
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));

        // Kiểm tra quyền xóa phòng (chỉ owner mới được xóa)
        if (!room.getOwnerId().equals(userId)) {
            throw new ApiException(MessageCode.UNAUTHORIZED);
        }

        // Xóa tất cả players trong phòng trước (3NF - manual delete)
        roomPlayerRepository.deleteByRoomId(roomId);

        // Xóa phòng
        roomRepository.deleteById(roomId);

        log.info("Room {} deleted by user {} - room players manually deleted", roomId, userId);

        // 🔥 Broadcast roomDeleted to all clients on RoomPage
        broadcastRoomDeleted(roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));
        return mapToRoomResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomByCode(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));
        return mapToRoomResponse(room);
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

        RoomResponse response = mapToRoomResponse(updatedRoom);

        // Broadcast host-changed and updated players list to room channel
        try {
            socketIOServer.getRoomOperations("room-" + roomId)
                    .sendEvent("host-changed", Map.of(
                            "roomId", roomId,
                            "newHostId", newHostId));
            List<RoomPlayerResponse> players = getRoomPlayers(roomId);
            socketIOServer.getRoomOperations("room-" + roomId)
                    .sendEvent("room-players", Map.of(
                            "roomId", roomId,
                            "players", players));
        } catch (Exception ignored) {
        }

        return response;
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

    @Override
    public PagedRoomResponse getAllRoomsSimple(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Room> roomPage;

        // Chỉ lấy phòng WAITING, không filter phức tạp
        if (search != null && !search.trim().isEmpty()) {
            roomPage = roomRepository.findAllRoomsByStatusAndSearch(RoomStatus.WAITING.name(), search.trim(), pageable);
        } else {
            roomPage = roomRepository.findAllRoomsByStatusWithPagination(RoomStatus.WAITING.name(), pageable);
        }

        return mapToPagedResponse(roomPage);
    }

    @Override
    public RoomResponse joinRoomById(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(MessageCode.ROOM_NOT_FOUND));

        if (room.getIsPrivate()) {
            throw new ApiException(MessageCode.ROOM_PERMISSION_DENIED);
        }

        if (room.getStatus().equals(RoomStatus.PLAYING.name()) || room.getStatus().equals(RoomStatus.FINISHED.name())) {
            throw new ApiException(MessageCode.ROOM_ALREADY_STARTED);
        }

        if (room.getStatus().equals(RoomStatus.FULL.name())) {
            throw new ApiException(MessageCode.ROOM_FULL);
        }

        Integer currentPlayers = roomPlayerRepository.countPlayersInRoom(room.getId());
        if (currentPlayers >= room.getMaxPlayers()) {
            throw new ApiException(MessageCode.ROOM_FULL);
        }

        if (roomPlayerRepository.existsByRoomIdAndUserId(room.getId(), userId)) {
            // User already in room - return success with room data (same as joinRoom
            // method)
            log.info("User {} already in room {}, returning room data", userId, room.getId());
            return mapToRoomResponse(room);
        }

        Integer nextJoinOrder = roomPlayerRepository.getMaxJoinOrderInRoom(room.getId()) + 1;

        RoomPlayers player = new RoomPlayers();
        player.setRoomId(room.getId());
        player.setUserId(userId);
        player.setIsHost(false);
        player.setJoinOrder(nextJoinOrder);
        player.setStatus("ACTIVE");
        roomPlayerRepository.save(player);

        updateRoomStatus(room);

        RoomResponse roomResponse = mapToRoomResponse(room);
        broadcastRoomUpdated(roomResponse);

        return roomResponse;
    }

    /**
     * Cập nhật trạng thái phòng dựa trên số lượng người chơi
     */
    private void updateRoomStatus(Room room) {
        if (room.getStatus().equals(RoomStatus.PLAYING.name()) ||
                room.getStatus().equals(RoomStatus.FINISHED.name()) ||
                room.getStatus().equals(RoomStatus.ARCHIVED.name())) {
            return; // Không thay đổi trạng thái nếu đang chơi hoặc đã kết thúc
        }

        Integer currentPlayers = roomPlayerRepository.countPlayersInRoom(room.getId());

        if (currentPlayers >= room.getMaxPlayers()) {
            room.setStatus(RoomStatus.FULL.name());
        } else {
            room.setStatus(RoomStatus.WAITING.name());
        }

        roomRepository.save(room);
    }

    @Override
    public List<RoomResponse> quickSearchRooms(String query) {
        List<Room> rooms = roomRepository.findPublicRoomsByRoomNameContaining(query);
        return rooms.stream()
                .limit(10)
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList());
    }

    private PagedRoomResponse mapToPagedResponse(Page<Room> roomPage) {
        PagedRoomResponse response = new PagedRoomResponse();
        response.setRooms(roomPage.getContent().stream()
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList()));
        response.setCurrentPage(roomPage.getNumber());
        response.setTotalPages(roomPage.getTotalPages());
        response.setTotalElements(roomPage.getTotalElements());
        response.setPageSize(roomPage.getSize());
        response.setHasNext(roomPage.hasNext());
        response.setHasPrevious(roomPage.hasPrevious());
        return response;
    }

    /**
     * Map Room entity to RoomResponse với currentPlayers và topicName
     */
    private RoomResponse mapToRoomResponse(Room room) {
        RoomResponse response = roomMapper.toResponse(room);

        // Set current players count
        Integer currentPlayers = roomPlayerRepository.countPlayersInRoom(room.getId());
        response.setCurrentPlayers(currentPlayers);

        // Load and set topic name
        if (room.getTopicId() != null) {
            topicRepository.findById(room.getTopicId())
                    .ifPresent(topic -> response.setTopicName(topic.getName()));
        }

        // Load and set owner username
        if (room.getOwnerId() != null) {
            userRepository.findById(room.getOwnerId())
                    .ifPresent(user -> response.setOwnerUsername(user.getUsername()));
        }

        return response;
    }

    private String generateUniqueRoomCode() {
        String roomCode;
        do {
            roomCode = roomCodeGenerator.generateRoomCode();
        } while (roomRepository.existsByRoomCode(roomCode));
        return roomCode;
    }

    /**
     * Broadcast room created event to clients in "room-list"
     */
    private void broadcastRoomCreated(RoomResponse room) {
        try {
            socketIOServer.getRoomOperations("room-list").sendEvent("roomCreated", Map.of(
                    "room", room,
                    "timestamp", System.currentTimeMillis()));
            log.info("📡 Broadcasted roomCreated event for room {} to room-list", room.getId());
        } catch (Exception e) {
            log.error("Failed to broadcast roomCreated: {}", e.getMessage());
        }
    }

    /**
     * Broadcast room deleted event to clients in "room-list"
     */
    private void broadcastRoomDeleted(Long roomId) {
        try {
            socketIOServer.getRoomOperations("room-list").sendEvent("roomDeleted", Map.of(
                    "roomId", roomId,
                    "timestamp", System.currentTimeMillis()));
            log.info("📡 Broadcasted roomDeleted event for room {}", roomId);
        } catch (Exception e) {
            log.error("Failed to broadcast roomDeleted: {}", e.getMessage());
        }
    }

    /**
     * Broadcast room updated event to clients in "room-list"
     */
    private void broadcastRoomUpdated(RoomResponse room) {
        try {
            socketIOServer.getRoomOperations("room-list").sendEvent("roomUpdated", Map.of(
                    "room", room,
                    "timestamp", System.currentTimeMillis()));
            log.info("📡 Broadcasted roomUpdated event for room {}", room.getId());
        } catch (Exception e) {
            log.error("Failed to broadcast roomUpdated: {}", e.getMessage());
        }
    }
}
