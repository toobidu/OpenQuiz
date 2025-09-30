package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.game.*;

public interface IGameService {
    void startGameSession(Long roomId);
    NextQuestionResponse getNextQuestion(Long roomId);
    QuestionResultResponse submitAnswer(Long roomId, Long userId, AnswerSubmitRequest request);
    GameOverResponse endGame(Long roomId);
    boolean isGameActive(Long roomId);
    int getRemainingTime(Long roomId);
}