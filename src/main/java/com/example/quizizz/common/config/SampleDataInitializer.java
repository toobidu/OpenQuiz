package com.example.quizizz.common.config;

import com.example.quizizz.model.entity.Answer;
import com.example.quizizz.model.entity.Question;
import com.example.quizizz.model.entity.Topic;
import com.example.quizizz.repository.AnswerRepository;
import com.example.quizizz.repository.QuestionRepository;
import com.example.quizizz.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Order(2) // Run after DataInitializer
@RequiredArgsConstructor
public class SampleDataInitializer implements CommandLineRunner {

    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting sample data initialization...");
        
        initializeTopics();
        initializeQuestions();
        
        log.info("Sample data initialization completed!");
    }

    private void initializeTopics() {
        List<Topic> topics = Arrays.asList(
            createTopic("Toán học", "Câu hỏi về toán học cơ bản"),
            createTopic("Lịch sử", "Câu hỏi về lịch sử Việt Nam và thế giới"),
            createTopic("Khoa học", "Câu hỏi về khoa học tự nhiên"),
            createTopic("Địa lý", "Câu hỏi về địa lý Việt Nam và thế giới"),
            createTopic("Văn học", "Câu hỏi về văn học Việt Nam")
        );

        topics.forEach(topic -> {
            if (!topicRepository.existsByName(topic.getName())) {
                topicRepository.save(topic);
                log.info("Created topic: {}", topic.getName());
            }
        });
    }

    private void initializeQuestions() {
        // Get topics
        Topic mathTopic = topicRepository.findAll().stream()
            .filter(t -> t.getName().equals("Toán học"))
            .findFirst().orElse(null);
        
        Topic historyTopic = topicRepository.findAll().stream()
            .filter(t -> t.getName().equals("Lịch sử"))
            .findFirst().orElse(null);

        if (mathTopic != null) {
            createMathQuestions(mathTopic.getId());
        }
        
        if (historyTopic != null) {
            createHistoryQuestions(historyTopic.getId());
        }
    }

    private void createMathQuestions(Long topicId) {
        // Math Question 1
        if (!questionRepository.existsByQuestionText("2 + 2 = ?")) {
            Question q1 = createQuestion("2 + 2 = ?", topicId, "MULTIPLE_CHOICE");
            q1 = questionRepository.save(q1);
            
            answerRepository.saveAll(Arrays.asList(
                createAnswer(q1.getId(), "3", false),
                createAnswer(q1.getId(), "4", true),
                createAnswer(q1.getId(), "5", false),
                createAnswer(q1.getId(), "6", false)
            ));
        }

        // Math Question 2
        if (!questionRepository.existsByQuestionText("10 x 5 = ?")) {
            Question q2 = createQuestion("10 x 5 = ?", topicId, "MULTIPLE_CHOICE");
            q2 = questionRepository.save(q2);
            
            answerRepository.saveAll(Arrays.asList(
                createAnswer(q2.getId(), "45", false),
                createAnswer(q2.getId(), "50", true),
                createAnswer(q2.getId(), "55", false),
                createAnswer(q2.getId(), "60", false)
            ));
        }

        // Math Question 3
        if (!questionRepository.existsByQuestionText("Căn bậc hai của 16 là?")) {
            Question q3 = createQuestion("Căn bậc hai của 16 là?", topicId, "MULTIPLE_CHOICE");
            q3 = questionRepository.save(q3);
            
            answerRepository.saveAll(Arrays.asList(
                createAnswer(q3.getId(), "2", false),
                createAnswer(q3.getId(), "4", true),
                createAnswer(q3.getId(), "6", false),
                createAnswer(q3.getId(), "8", false)
            ));
        }
    }

    private void createHistoryQuestions(Long topicId) {
        // History Question 1
        if (!questionRepository.existsByQuestionText("Việt Nam giành độc lập vào năm nào?")) {
            Question q1 = createQuestion("Việt Nam giành độc lập vào năm nào?", topicId, "MULTIPLE_CHOICE");
            q1 = questionRepository.save(q1);
            
            answerRepository.saveAll(Arrays.asList(
                createAnswer(q1.getId(), "1944", false),
                createAnswer(q1.getId(), "1945", true),
                createAnswer(q1.getId(), "1946", false),
                createAnswer(q1.getId(), "1947", false)
            ));
        }

        // History Question 2
        if (!questionRepository.existsByQuestionText("Ai là người sáng lập ra Đảng Cộng sản Việt Nam?")) {
            Question q2 = createQuestion("Ai là người sáng lập ra Đảng Cộng sản Việt Nam?", topicId, "MULTIPLE_CHOICE");
            q2 = questionRepository.save(q2);
            
            answerRepository.saveAll(Arrays.asList(
                createAnswer(q2.getId(), "Hồ Chí Minh", true),
                createAnswer(q2.getId(), "Võ Nguyên Giáp", false),
                createAnswer(q2.getId(), "Phạm Văn Đồng", false),
                createAnswer(q2.getId(), "Lê Duẩn", false)
            ));
        }
    }

    private Topic createTopic(String name, String description) {
        Topic topic = new Topic();
        topic.setName(name);
        topic.setDescription(description);
        return topic;
    }

    private Question createQuestion(String questionText, Long topicId, String questionType) {
        Question question = new Question();
        question.setQuestionText(questionText);
        question.setTopicId(topicId);
        question.setQuestionType(questionType);
        return question;
    }

    private Answer createAnswer(Long questionId, String answerText, boolean isCorrect) {
        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setAnswerText(answerText);
        answer.setIsCorrect(isCorrect);
        return answer;
    }
}