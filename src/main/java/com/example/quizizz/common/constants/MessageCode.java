package com.example.quizizz.common.constants;

public enum MessageCode {
    SUCCESS("MSG_0000", "Operation successful"),
    VALIDATION_ERROR("ERR_0001", "Validation error"),
    INTERNAL_ERROR("ERR_0002", "Internal server error"),
    UNAUTHORIZED("ERR_0003", "Unauthorized access"),
    FORBIDDEN("ERR_0004", "Access forbidden"),
    NOT_FOUND("ERR_0005", "Resource not found"),
    BAD_REQUEST("ERR_0006", "Bad request"),
    
    USER_CREATED("MSG_1001", "User created successfully"),
    USER_NOT_FOUND("ERR_1001", "User not found"),
    USER_ALREADY_EXISTS("ERR_1002", "User already exists"),
    
    AUTH_LOGIN_SUCCESS("MSG_2001", "Login successful"),
    AUTH_LOGOUT_SUCCESS("MSG_2002", "Logout successful"),
    AUTH_TOKEN_REFRESHED("MSG_2003", "Token refreshed successfully"),
    AUTH_PASSWORD_RESET_SUCCESS("MSG_2004", "Password reset successfully"),
    AUTH_INVALID_TOKEN("ERR_2001", "Invalid or expired token"),
    AUTH_PASSWORD_INCORRECT("ERR_2004", "Incorrect password"),
    AUTH_EMAIL_SEND_FAILED("ERR_2006", "Failed to send email"),
    AUTH_PASSWORD_RESET_FAILED("ERR_2005", "Password reset failed"),
    
    PERMISSION_GRANTED("MSG_3001", "Permission granted successfully"),
    PERMISSION_REVOKED("MSG_3002", "Permission revoked successfully"),
    ROLE_ASSIGNED("MSG_3003", "Role assigned successfully"),
    ROLE_REMOVED("MSG_3004", "Role removed successfully"),
    ROLE_NOT_FOUND("ERR_3002", "Role not found"),
    
    ROOM_NOT_FOUND("ERR_4001", "Room not found"),
    ROOM_FULL("ERR_4002", "Room is full"),
    ROOM_ALREADY_STARTED("ERR_4003", "Room game already started"),
    ROOM_PERMISSION_DENIED("ERR_4005", "Permission denied for this room action"),
    ROOM_ALREADY_JOINED("ERR_4006", "User already joined this room"),
    ROOM_NOT_JOINED("ERR_4007", "User has not joined this room"),
    ROOM_INVALID_MAX_PLAYERS("ERR_4009", "Invalid maximum players for this mode"),
    
    GAME_ALREADY_STARTED("ERR_7002", "Game has already started"),
    PLAYER_NOT_IN_GAME("ERR_7007", "Player is not in the game"),
    
    FILE_UPLOADED("MSG_10001", "File uploaded successfully"),
    AVATAR_UPDATED("MSG_10003", "Avatar updated successfully"),
    AVATAR_URL_RETRIEVED("MSG_10004", "Avatar URL retrieved successfully"),
    
    EMPTY_FILE("ERR_10005", "File cannot be empty"),
    INVALID_FILE_TYPE("ERR_10006", "Invalid file type"),
    FILE_TOO_LARGE("ERR_10003", "File size too large"),
    INTERNAL_SERVER_ERROR("ERR_0002", "Internal server error"),
    PERMISSION_NOT_FOUND("ERR_3003", "Permission not found"),
    PERMISSION_ALREADY_EXISTS("ERR_3005", "Permission already exists"),
    ROLE_ALREADY_EXISTS("ERR_3006", "Role already exists"),
    
    ROOM_CREATED("MSG_4001", "Room created successfully"),
    ROOM_UPDATED("MSG_4002", "Room updated successfully"),
    ROOM_DELETED("MSG_4003", "Room deleted successfully"),
    ROOM_JOINED("MSG_4004", "Joined room successfully"),
    ROOM_LEFT("MSG_4005", "Left room successfully"),
    ROOM_HOST_TRANSFERRED("MSG_4006", "Host transferred successfully"),
    
    TOPIC_CREATED("MSG_5001", "Topic created successfully"),
    TOPIC_UPDATED("MSG_5002", "Topic updated successfully"),
    TOPIC_DELETED("MSG_5003", "Topic deleted successfully"),
    
    GAME_STARTED("MSG_7001", "Game started successfully"),
    PLAYER_LEFT_GAME("MSG_7006", "Player left game successfully"),
    
    NOT_IMPLEMENTED("ERR_9999", "Feature not implemented yet");

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