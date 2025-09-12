package com.example.quizizz.repository;

import com.example.quizizz.enums.RoomStatus;
import com.example.quizizz.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho quản lý phòng chơi
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    /**
     * Tìm phòng theo room code
     */
    Optional<Room> findByRoomCode(String roomCode);
    
    /**
     * Kiểm tra room code đã tồn tại chưa
     */
    boolean existsByRoomCode(String roomCode);
    
    /**
     * Lấy danh sách phòng của owner
     */
    List<Room> findByOwnerId(Long ownerId);
    
    /**
     * Lấy danh sách phòng public theo trạng thái (loại trừ ARCHIVED)
     */
    @Query("SELECT r FROM Room r WHERE r.isPrivate = false AND r.status = :status AND r.status != 'ARCHIVED'")
    List<Room> findPublicRoomsByStatus(@Param("status") RoomStatus status);
    
    /**
     * Tìm kiếm phòng public theo tên (loại trừ ARCHIVED)
     */
    @Query("SELECT r FROM Room r WHERE r.isPrivate = false AND r.roomName LIKE %:roomName% AND r.status != 'ARCHIVED'")
    List<Room> findPublicRoomsByRoomNameContaining(@Param("roomName") String roomName);
    
    /**
     * Lấy danh sách phòng theo topic
     */
    List<Room> findByTopicId(Long topicId);
    
    /**
     * Lấy danh sách phòng đang chờ (WAITING) và public
     */
    @Query("SELECT r FROM Room r WHERE r.isPrivate = false AND r.status = 'WAITING' ORDER BY r.createdAt DESC")
    List<Room> findPublicWaitingRooms();
}