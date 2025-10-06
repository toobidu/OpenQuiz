package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.answer.*;
import java.util.List;

public interface IAnswerService {
    AnswerResponse createAnswer(CreateAnswerRequest request);
    AnswerResponse updateAnswer(Long id, UpdateAnswerRequest request);
    void deleteAnswer(Long id);
    AnswerResponse getAnswerById(Long id);
    List<AnswerResponse> getAnswersByQuestionId(Long questionId);
}