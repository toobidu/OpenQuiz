package com.example.quizizz.service.Implement;

import com.example.quizizz.model.dto.answer.*;
import com.example.quizizz.model.entity.Answer;
import com.example.quizizz.repository.AnswerRepository;
import com.example.quizizz.mapper.AnswerMapper;
import com.example.quizizz.service.Interface.IAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerServiceImplement implements IAnswerService {
    
    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;
    
    @Override
    public AnswerResponse createAnswer(CreateAnswerRequest request) {
        Answer answer = answerMapper.toEntity(request);
        Answer savedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(savedAnswer);
    }
    
    @Override
    public AnswerResponse updateAnswer(Long id, UpdateAnswerRequest request) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        answerMapper.updateEntityFromRequest(answer, request);
        Answer updatedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(updatedAnswer);
    }
    
    @Override
    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }
    
    @Override
    public AnswerResponse getAnswerById(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        return answerMapper.toResponse(answer);
    }
    
    @Override
    public List<AnswerResponse> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answers.stream()
                .map(answerMapper::toResponse)
                .collect(Collectors.toList());
    }
}