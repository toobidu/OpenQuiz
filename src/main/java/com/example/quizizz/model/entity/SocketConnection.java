package com.example.quizizz.model.entity;

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
@Table(name = "socket_connections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketConnection {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "socket_id", nullable = false, unique = true)
    private String socketId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_connected", nullable = false)
    private boolean isConnected;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "connection_time", nullable = false)
    private LocalDateTime connectionTime;

    @Column(name = "disconnection_time")
    private LocalDateTime disconnectionTime;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
