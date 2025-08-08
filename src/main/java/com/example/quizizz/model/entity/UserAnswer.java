package com.example.quizizz.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "answer_id", nullable = false)
    private Long answerId;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    @Column(name = "score", nullable = false)
    private Integer score;

    /**
     * Tổng thời gian chơi tính bằng giây.
     */
    @Column(name = "time_taken", nullable = false)
    private Integer timeTaken; 

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
