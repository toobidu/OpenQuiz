package com.example.quizizz.model.dto.room;

import com.example.quizizz.common.constants.RoomMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {

    @NotBlank(message = "Room name is required")
    private String roomName;

    @NotNull(message = "Room mode is required")
    private RoomMode roomMode;

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    private Boolean isPrivate = false;

    @Min(value = 2, message = "Max players must be at least 2 for ONE_VS_ONE mode")
    private Integer maxPlayers;

    /**
     * Số lượng câu hỏi trong phòng
     */
    @NotNull(message = "Question count is required")
    @Min(value = 1, message = "Question count must be at least 1")
    private Integer questionCount;

    /**
     * Loại câu hỏi (MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK)
     */
    private String questionType;

    /**
     * Thời gian đếm ngược cho mỗi câu hỏi (giây)
     */
    @NotNull(message = "Countdown time is required")
    @Min(value = 5, message = "Countdown time must be at least 5 seconds")
    private Integer countdownTime;

    @NotNull(message = "Time limit is required")
    private Duration timeLimit;
}
