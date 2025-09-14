package com.example.quizizz.controller;

import com.example.quizizz.common.config.ApiResponse;
import com.example.quizizz.common.constants.MessageCode;
import com.example.quizizz.model.dto.question.QuestionWithAnswersResponse;
import com.example.quizizz.service.Interface.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Question", description = "APIs liên quan đến câu hỏi")
public class QuestionController {

    private final IQuestionService questionService;

    @Operation(summary = "Lấy câu hỏi ngẫu nhiên với đáp án", description = "Lấy câu hỏi ngẫu nhiên kèm đáp án theo topic và loại câu hỏi")
    @GetMapping("/random")
    public ResponseEntity<ApiResponse<List<QuestionWithAnswersResponse>>> getRandomQuestions(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String questionType,
            @RequestParam(defaultValue = "10") int count) {
        
        List<QuestionWithAnswersResponse> questions = questionService.getRandomQuestionsWithAnswers(topicId, questionType, count);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, questions));
    }

    @Operation(summary = "Lấy câu hỏi cho người chơi cụ thể", description = "Lấy câu hỏi ngẫu nhiên khác nhau cho mỗi người chơi")
    @GetMapping("/random/player/{playerId}")
    public ResponseEntity<ApiResponse<List<QuestionWithAnswersResponse>>> getRandomQuestionsForPlayer(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String questionType,
            @RequestParam(defaultValue = "10") int count) {
        
        List<QuestionWithAnswersResponse> questions = questionService.getRandomQuestionsForPlayer(topicId, questionType, count, playerId);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, questions));
    }

    @Operation(summary = "Đếm số câu hỏi có sẵn", description = "Đếm số câu hỏi có sẵn theo topic và loại câu hỏi")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countAvailableQuestions(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String questionType) {
        
        long count = questionService.countAvailableQuestions(topicId, questionType);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, count));
    }
}
