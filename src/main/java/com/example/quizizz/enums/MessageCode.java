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

    // ============ ROOM ============
    ROOM_CREATED("MSG_4001", "Room created successfully"),
    ROOM_UPDATED("MSG_4002", "Room updated successfully"),
    ROOM_DELETED("MSG_4003", "Room deleted successfully"),
    ROOM_JOINED("MSG_4004", "Joined room successfully"),
    ROOM_LEFT("MSG_4005", "Left room successfully"),
    ROOM_HOST_TRANSFERRED("MSG_4006", "Host transferred successfully"),
    ROOM_NOT_FOUND("ERR_4001", "Room not found"),
    ROOM_FULL("ERR_4002", "Room is full"),
    ROOM_ALREADY_STARTED("ERR_4003", "Room game already started"),
    ROOM_CODE_INVALID("ERR_4004", "Invalid room code"),
    ROOM_PERMISSION_DENIED("ERR_4005", "Permission denied for this room action"),
    ROOM_ALREADY_JOINED("ERR_4006", "User already joined this room"),
    ROOM_NOT_JOINED("ERR_4007", "User has not joined this room"),
    ROOM_INVALID_MODE("ERR_4008", "Invalid room mode"),
    ROOM_INVALID_MAX_PLAYERS("ERR_4009", "Invalid maximum players for this mode"),

    // ============ TOPIC ============
    TOPIC_CREATED("MSG_5001", "Topic created successfully"),
    TOPIC_UPDATED("MSG_5002", "Topic updated successfully"),
    TOPIC_DELETED("MSG_5003", "Topic deleted successfully"),
    TOPIC_ALREADY_EXISTS("ERR_5004", "Topic already exists"),
    TOPIC_DELETE_FAILED("ERR_5005", "Delete topic failed"),
    TOPIC_UPDATE_FAILED("ERR_5006", "Update topic failed"),
    TOPIC_CREATE_FAILED("ERR_5007", "Create topic failed"),
    TOPIC_NOT_FOUND("ERR_5001", "Topic not found"),
    TOPIC_INVALID("ERR_5002", "Invalid topic"),

    // ============ QUIZ & QUESTIONS ============
    QUIZ_CREATED("MSG_6001", "Quiz created successfully"),
    QUIZ_UPDATED("MSG_6002", "Quiz updated successfully"),
    QUIZ_DELETED("MSG_6003", "Quiz deleted successfully"),
    QUIZ_PUBLISHED("MSG_6004", "Quiz published successfully"),
    QUESTION_CREATED("MSG_6005", "Question created successfully"),
    QUESTION_UPDATED("MSG_6006", "Question updated successfully"),
    QUESTION_DELETED("MSG_6007", "Question deleted successfully"),
    QUIZ_NOT_FOUND("ERR_6001", "Quiz not found"),
    QUESTION_NOT_FOUND("ERR_6002", "Question not found"),
    QUIZ_ALREADY_PUBLISHED("ERR_6003", "Quiz is already published"),
    QUIZ_NOT_PUBLISHED("ERR_6004", "Quiz is not published yet"),
    INVALID_QUESTION_TYPE("ERR_6005", "Invalid question type"),
    ANSWER_NOT_FOUND("ERR_6006", "Answer not found"),

    // ============ GAME SESSION ============
    GAME_STARTED("MSG_7001", "Game started successfully"),
    GAME_ENDED("MSG_7002", "Game ended successfully"),
    GAME_PAUSED("MSG_7003", "Game paused successfully"),
    GAME_RESUMED("MSG_7004", "Game resumed successfully"),
    PLAYER_JOINED_GAME("MSG_7005", "Player joined game successfully"),
    PLAYER_LEFT_GAME("MSG_7006", "Player left game successfully"),
    GAME_NOT_FOUND("ERR_7001", "Game session not found"),
    GAME_ALREADY_STARTED("ERR_7002", "Game has already started"),
    GAME_NOT_STARTED("ERR_7003", "Game has not started yet"),
    GAME_ALREADY_ENDED("ERR_7004", "Game has already ended"),
    INVALID_GAME_STATUS("ERR_7005", "Invalid game status"),
    PLAYER_ALREADY_IN_GAME("ERR_7006", "Player is already in the game"),
    PLAYER_NOT_IN_GAME("ERR_7007", "Player is not in the game"),

    // ============ ANSWERS & SCORING ============
    ANSWER_SUBMITTED("MSG_8001", "Answer submitted successfully"),
    SCORE_CALCULATED("MSG_8002", "Score calculated successfully"),
    LEADERBOARD_UPDATED("MSG_8003", "Leaderboard updated successfully"),
    INVALID_ANSWER("ERR_8001", "Invalid answer"),
    ANSWER_TIME_EXPIRED("ERR_8002", "Answer time has expired"),
    ALREADY_ANSWERED("ERR_8003", "Question already answered"),

    // ============ SOCKET & REAL-TIME ============
    SOCKET_CONNECTED("MSG_9001", "Socket connected successfully"),
    SOCKET_DISCONNECTED("MSG_9002", "Socket disconnected"),
    EVENT_BROADCASTED("MSG_9003", "Event broadcasted successfully"),
    SOCKET_CONNECTION_FAILED("ERR_9001", "Socket connection failed"),
    INVALID_SOCKET_EVENT("ERR_9002", "Invalid socket event"),

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
    private final String message;

    MessageCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}