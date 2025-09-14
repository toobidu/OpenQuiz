package com.example.quizizz.model.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho việc mời người chơi vào phòng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitePlayerRequest {
    
    /**
     * Username của người được mời
     */
    @NotBlank(message = "Username is required")
    private String username;
    
    /**
     * Tin nhắn kèm theo lời mời (tùy chọn)
     */
    private String message;
    
    /**
     * ID phòng
     */
    @NotNull(message = "Room ID is required")
    private Long roomId;
}
