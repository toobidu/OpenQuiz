package com.example.quizizz.model.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO response cho lời mời vào phòng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {
    
    private Long id;
    private Long roomId;
    private String roomName;
    private Long invitedUserId;
    private String invitedUsername;
    private Long invitedBy;
    private String inviterUsername;
    private String status;
    private String message;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}