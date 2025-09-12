package com.example.quizizz.model.entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", nullable = false, unique = true)
    private String roomCode;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "room_mode", nullable = false)
    private String roomMode;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "is_private")
    private Boolean isPrivate;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    /**
     * Số lượng câu hỏi trong phòng
     */
    @Column(name = "question_count", nullable = false)
    private Integer questionCount;

    /**
     * Loại câu hỏi (MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK)
     */
    @Column(name = "question_type")
    private String questionType;

    /**
     * Thời gian đếm ngược cho mỗi câu hỏi (giây)
     */
    @Column(name = "countdown_time", nullable = false)
    private Integer countdownTime;

    /**
     * Đánh dấu phòng đã có lịch sử game (không được xóa)
     */
    @Column(name = "has_game_history", nullable = false)
    private Boolean hasGameHistory = false;

    /**
     * Thời gian giới hạn cho phiên chơi.
     * Sử dụng Duration để lưu trữ thời gian.
     */
    @Column(name = "time_limit")
    private Duration timeLimit;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
