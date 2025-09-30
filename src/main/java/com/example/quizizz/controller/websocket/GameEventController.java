package com.example.quizizz.controller.websocket;

import com.example.quizizz.model.dto.game.*;
import com.example.quizizz.model.dto.websocket.WebSocketMessage;
import com.example.quizizz.service.Interface.IGameService;
import com.example.quizizz.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameEventController {

    private final IGameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;

    /**
     * Xử lý sự kiện gửi câu hỏi tiếp theo
     * Client gửi: /app/game/{roomId}/next-question
     * Broadcast đến: /topic/game/{roomId}
     */
    @MessageMapping("/game/{roomId}/next-question")
    public void handleNextQuestion(@DestinationVariable Long roomId) {
        try {
            NextQuestionResponse response = gameService.getNextQuestion(roomId);
            if (response != null) {
                WebSocketMessage<NextQuestionResponse> message = WebSocketMessage.success("NEXT_QUESTION", response);
                messagingTemplate.convertAndSend("/topic/game/" + roomId, message);
            } else {
                // Game over
                GameOverResponse gameOver = gameService.endGame(roomId);
                WebSocketMessage<GameOverResponse> message = WebSocketMessage.success("GAME_OVER", gameOver);
                messagingTemplate.convertAndSend("/topic/game/" + roomId, message);
            }
        } catch (Exception e) {
            log.error("Error sending next question for room {}: ", roomId, e);
            WebSocketMessage<String> errorMessage = WebSocketMessage.error("NEXT_QUESTION_ERROR", e.getMessage());
            messagingTemplate.convertAndSend("/topic/game/" + roomId, errorMessage);
        }
    }

    /**
     * Xử lý sự kiện người chơi trả lời
     * Client gửi: /app/game/{roomId}/answer
     * Broadcast đến: /topic/game/{roomId}/result
     */
    @MessageMapping("/game/{roomId}/answer")
    public void handleAnswerSubmit(@DestinationVariable Long roomId,
                                   @Payload AnswerSubmitRequest request,
                                   SimpMessageHeaderAccessor headerAccessor) {
        try {
            String token = (String) headerAccessor.getSessionAttributes().get("token");
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = Long.parseLong(username);

            QuestionResultResponse response = gameService.submitAnswer(roomId, userId, request);
            WebSocketMessage<QuestionResultResponse> message = WebSocketMessage.success("QUESTION_RESULT", response);
            messagingTemplate.convertAndSend("/topic/game/" + roomId + "/result", message);
        } catch (Exception e) {
            log.error("Error submitting answer for room {}: ", roomId, e);
            WebSocketMessage<String> errorMessage = WebSocketMessage.error("ANSWER_SUBMIT_ERROR", e.getMessage());
            messagingTemplate.convertAndSend("/topic/game/" + roomId + "/result", errorMessage);
        }
    }

    /**
     * Xử lý sự kiện kết thúc game
     * Client gửi: /app/game/{roomId}/end
     * Broadcast đến: /topic/game/{roomId}
     */
    @MessageMapping("/game/{roomId}/end")
    public void handleGameEnd(@DestinationVariable Long roomId) {
        try {
            GameOverResponse response = gameService.endGame(roomId);
            WebSocketMessage<GameOverResponse> message = WebSocketMessage.success("GAME_OVER", response);
            messagingTemplate.convertAndSend("/topic/game/" + roomId, message);
        } catch (Exception e) {
            log.error("Error ending game for room {}: ", roomId, e);
            WebSocketMessage<String> errorMessage = WebSocketMessage.error("GAME_END_ERROR", e.getMessage());
            messagingTemplate.convertAndSend("/topic/game/" + roomId, errorMessage);
        }
    }
}