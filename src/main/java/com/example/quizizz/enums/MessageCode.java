package com.example.quizizz.enums;

public enum MessageCode {
    // ============ GENERAL ============
    SUCCESS("MSG_0000", "Operation successful"),
    VALIDATION_ERROR("ERR_0001", "Validation error"),
    INTERNAL_ERROR("ERR_0002", "Internal server error"),
    INTERNAL_SERVER_ERROR("ERR_0002", "Internal server error"),
    UNAUTHORIZED("ERR_0003", "Unauthorized access"),
    FORBIDDEN("ERR_0004", "Access forbidden"),
    NOT_FOUND("ERR_0005", "Resource not found"),
    BAD_REQUEST("ERR_0006", "Bad request"),
    INVALID_INPUT("ERR_0007", "Invalid input data"),
    
    // ============ USER ============
    USER_CREATED("MSG_1001", "User created successfully"),
    USER_UPDATED("MSG_1002", "User updated successfully"),
    USER_DELETED("MSG_1003", "User deleted successfully"),
    USER_NOT_FOUND("ERR_1001", "User not found"),
    USER_ALREADY_EXISTS("ERR_1002", "User already exists"),
    USER_INVALID_CREDENTIALS("ERR_1003", "Invalid username or password"),
    USER_ACCOUNT_LOCKED("ERR_1004", "User account is locked"),
    USER_ACCOUNT_DISABLED("ERR_1005", "User account is disabled"),
    
    // ============ AUTHENTICATION ============
    AUTH_LOGIN_SUCCESS("MSG_2001", "Login successful"),
    AUTH_LOGOUT_SUCCESS("MSG_2002", "Logout successful"),
    AUTH_TOKEN_REFRESHED("MSG_2003", "Token refreshed successfully"),
    AUTH_PASSWORD_RESET_SUCCESS("MSG_2004", "Password reset successfully"),
    AUTH_PASSWORD_RESET_EMAIL_SENT("MSG_2005", "Password reset email sent successfully"),
    AUTH_ALL_DEVICES_LOGGED_OUT("MSG_2006", "All devices logged out successfully"),
    AUTH_INVALID_TOKEN("ERR_2001", "Invalid or expired token"),
    AUTH_TOKEN_REQUIRED("ERR_2002", "Authentication token required"),
    AUTH_LOGIN_FAILED("ERR_2003", "Login failed"),
    AUTH_PASSWORD_INCORRECT("ERR_2004", "Incorrect password"),
    AUTH_PASSWORD_RESET_FAILED("ERR_2005", "Password reset failed"),
    AUTH_EMAIL_SEND_FAILED("ERR_2006", "Failed to send email"),

    // ============ PERMISSIONS & ROLES ============
    PERMISSION_GRANTED("MSG_3001", "Permission granted successfully"),
    PERMISSION_REVOKED("MSG_3002", "Permission revoked successfully"),
    ROLE_ASSIGNED("MSG_3003", "Role assigned successfully"),
    ROLE_REMOVED("MSG_3004", "Role removed successfully"),
    PERMISSION_DENIED("ERR_3001", "Permission denied"),
    ROLE_NOT_FOUND("ERR_3002", "Role not found"),
    PERMISSION_NOT_FOUND("ERR_3003", "Permission not found"),
    INSUFFICIENT_PRIVILEGES("ERR_3004", "Insufficient privileges"),
    PERMISSION_ALREADY_EXISTS("ERR_3005", "Permission already exists"),
    ROLE_ALREADY_EXISTS("ERR_3006", "Role already exists"),
    ROLE_DELETE_FAILED("ERR_3007", "Delete role failed"),
    PERMISSION_DELETE_FAILED("ERR_3008", "Delete permission failed"),
    
    // ============ QUIZ & QUESTIONS ============
    QUIZ_CREATED("MSG_4001", "Quiz created successfully"),
    QUIZ_UPDATED("MSG_4002", "Quiz updated successfully"),
    QUIZ_DELETED("MSG_4003", "Quiz deleted successfully"),
    QUIZ_PUBLISHED("MSG_4004", "Quiz published successfully"),
    QUESTION_CREATED("MSG_4005", "Question created successfully"),
    QUESTION_UPDATED("MSG_4006", "Question updated successfully"),
    QUESTION_DELETED("MSG_4007", "Question deleted successfully"),
    QUIZ_NOT_FOUND("ERR_4001", "Quiz not found"),
    QUESTION_NOT_FOUND("ERR_4002", "Question not found"),
    QUIZ_ALREADY_PUBLISHED("ERR_4003", "Quiz is already published"),
    QUIZ_NOT_PUBLISHED("ERR_4004", "Quiz is not published yet"),
    INVALID_QUESTION_TYPE("ERR_4005", "Invalid question type"),
    ANSWER_NOT_FOUND("ERR_4006", "Answer not found"),
    TOPIC_NOT_FOUND("ERR_4007", "Topic not found"),
    
    // ============ ROOM & GAME SESSION ============
    ROOM_CREATED("MSG_5001", "Room created successfully"),
    ROOM_UPDATED("MSG_5002", "Room updated successfully"),
    ROOM_DELETED("MSG_5003", "Room deleted successfully"),
    PLAYER_JOINED_ROOM("MSG_5004", "Player joined room successfully"),
    PLAYER_LEFT_ROOM("MSG_5005", "Player left room successfully"),
    GAME_STARTED("MSG_5006", "Game started successfully"),
    GAME_ENDED("MSG_5007", "Game ended successfully"),
    GAME_PAUSED("MSG_5008", "Game paused successfully"),
    GAME_RESUMED("MSG_5009", "Game resumed successfully"),
    ROOM_NOT_FOUND("ERR_5001", "Room not found"),
    ROOM_FULL("ERR_5002", "Room is full"),
    ROOM_NOT_AVAILABLE("ERR_5003", "Room is not available"),
    PLAYER_ALREADY_IN_ROOM("ERR_5004", "Player is already in the room"),
    PLAYER_NOT_IN_ROOM("ERR_5005", "Player is not in the room"),
    GAME_NOT_FOUND("ERR_5006", "Game session not found"),
    GAME_ALREADY_STARTED("ERR_5007", "Game has already started"),
    GAME_NOT_STARTED("ERR_5008", "Game has not started yet"),
    GAME_ALREADY_ENDED("ERR_5009", "Game has already ended"),
    INVALID_ROOM_STATUS("ERR_5010", "Invalid room status"),
    INVALID_GAME_STATUS("ERR_5011", "Invalid game status"),
    
    // ============ PLAYER PROFILE & RANK ============
    PROFILE_UPDATED("MSG_6001", "Player profile updated successfully"),
    RANK_UPDATED("MSG_6002", "Player rank updated successfully"),
    PROFILE_NOT_FOUND("ERR_6001", "Player profile not found"),
    RANK_NOT_FOUND("ERR_6002", "Rank not found"),
    INVALID_RANK("ERR_6003", "Invalid rank"),
    
    // ============ ANSWERS & SCORING ============
    ANSWER_SUBMITTED("MSG_7001", "Answer submitted successfully"),
    SCORE_CALCULATED("MSG_7002", "Score calculated successfully"),
    LEADERBOARD_UPDATED("MSG_7003", "Leaderboard updated successfully"),
    INVALID_ANSWER("ERR_7001", "Invalid answer"),
    ANSWER_TIME_EXPIRED("ERR_7002", "Answer time has expired"),
    ALREADY_ANSWERED("ERR_7003", "Question already answered"),
    
    // ============ SOCKET & REAL-TIME ============
    SOCKET_CONNECTED("MSG_8001", "Socket connected successfully"),
    SOCKET_DISCONNECTED("MSG_8002", "Socket disconnected"),
    EVENT_BROADCASTED("MSG_8003", "Event broadcasted successfully"),
    SOCKET_CONNECTION_FAILED("ERR_8001", "Socket connection failed"),
    INVALID_SOCKET_EVENT("ERR_8002", "Invalid socket event"),
    
    // ============ CACHE & REDIS ============
    CACHE_UPDATED("MSG_9001", "Cache updated successfully"),
    CACHE_CLEARED("MSG_9002", "Cache cleared successfully"),
    CACHE_ERROR("ERR_9001", "Cache operation failed"),
    REDIS_CONNECTION_ERROR("ERR_9002", "Redis connection error"),
    
    // ============ FILE & UPLOAD ============
    FILE_UPLOADED("MSG_10001", "File uploaded successfully"),
    FILE_DELETED("MSG_10002", "File deleted successfully"),
    AVATAR_UPDATED("MSG_10003", "Avatar updated successfully"),
    AVATAR_URL_RETRIEVED("MSG_10004", "Avatar URL retrieved successfully"),
    FILE_NOT_FOUND("ERR_10001", "File not found"),
    INVALID_FILE_FORMAT("ERR_10002", "Invalid file format"),
    FILE_TOO_LARGE("ERR_10003", "File size too large"),
    UPLOAD_FAILED("ERR_10004", "File upload failed"),
    EMPTY_FILE("ERR_10005", "File cannot be empty"),
    INVALID_FILE_TYPE("ERR_10006", "Invalid file type");

    private final String code;
    private final String defaultMessage;

    MessageCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}