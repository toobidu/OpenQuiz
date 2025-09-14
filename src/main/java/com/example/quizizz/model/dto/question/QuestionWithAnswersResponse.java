package com.example.quizizz.model.dto.question;

import com.example.quizizz.model.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionWithAnswersResponse {
    private Long id;
    private String questionText;
    private Long topicId;
    private String questionType;
    private List<Answer> answers;
}
