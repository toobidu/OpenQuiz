package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.question.QuestionWithAnswersResponse;
import java.util.List;

public interface IQuestionService {
    List<QuestionWithAnswersResponse> getRandomQuestionsWithAnswers(Long topicId, String questionType, int count);
    List<QuestionWithAnswersResponse> getRandomQuestionsForPlayer(Long topicId, String questionType, int count, Long playerId);
    long countAvailableQuestions(Long topicId, String questionType);
}
