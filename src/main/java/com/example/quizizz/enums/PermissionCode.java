package com.example.quizizz.enums;

public enum PermissionCode {
    /**
     * User permissions
     */
    USER_READ,
    USER_WRITE,
    USER_DELETE,


    /**
     * Quiz permissions
     */
    QUIZ_CREATE,
    QUIZ_EDIT,
    QUIZ_DELETE,
    QUIZ_PUBLISH,

    /**
     * Question permissions
     */
    ROOM_CREATE,
    ROOM_MANAGE,
    ROOM_JOIN,
    ROOM_KICK_PLAYER,

    /**
     * Game permissions
     */
    GAME_START,
    GAME_PAUSE,
    GAME_END,
    GAME_MODERATE,

    /**
     * Admin permissions
     */
    ADMIN_FULL_ACCESS,
    SYSTEM_CONFIG
}