package com.example.quizizz.repository;

import com.example.quizizz.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    boolean existsByQuestionText(String questionText);
    List<Question> findQuestionByTopicId(Long topicId);

}
