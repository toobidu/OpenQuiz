package com.example.quizizz.service.helper;

import org.springframework.stereotype.Component;

/**
 * Calculator for game scores based on accuracy and speed
 * Similar to Kahoot/Quizizz scoring system
 */
@Component
public class GameScoreCalculator {
    
    private static final int BASE_SCORE = 100;
    private static final int MAX_SPEED_BONUS = 50;
    
    /**
     * Calculate score based on correctness, time taken, and time limit
     * 
     * @param isCorrect Whether the answer is correct
     * @param timeTaken Time taken to answer (in milliseconds)
     * @param timeLimit Time limit for the question (in seconds)
     * @return Calculated score
     */
    public int calculateScore(boolean isCorrect, Long timeTaken, int timeLimit) {
        if (!isCorrect) {
            return 0;
        }
        
        // Convert time limit to milliseconds
        long timeLimitMs = timeLimit * 1000L;
        
        // Calculate speed bonus (faster = more points)
        double speedRatio = Math.max(0, (double)(timeLimitMs - timeTaken) / timeLimitMs);
        int speedBonus = (int)(MAX_SPEED_BONUS * speedRatio);
        
        return BASE_SCORE + speedBonus;
    }
}