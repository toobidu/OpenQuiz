package com.example.quizizz.model.dto.room;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho việc kick người chơi khỏi phòng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KickPlayerRequest {
    
    /**
     * ID người chơi bị kick
     */
    @NotNull(message = "Player ID is required")
    private Long playerId;
    
    /**
     * Lý do kick (tùy chọn)
     */
    private String reason;
}
