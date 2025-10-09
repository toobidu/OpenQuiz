package com.example.quizizz.service.Interface;

import com.example.quizizz.common.constants.GameStatus;
import com.example.quizizz.common.constants.PermissionCode;

import java.util.Map;
import java.util.Set;

public interface IRedisService {
    // Token blacklist
    void blacklistToken(String token, long expirationTime);
    boolean isTokenBlacklisted(String token);
    void addTokenToBlacklistWithRefreshTTL(String token, long ttl);
    
    // User permissions cache
    void cacheUserPermissions(Long userId, Set<PermissionCode> permissions);
    Set<PermissionCode> getUserPermissions(Long userId);
    void clearUserPermissions(Long userId);
    void saveUserPermissions(Long userId, Set<PermissionCode> permissions);
    void deleteUserPermissionsCache(Long userId);
    
    // User online status
    void setUserOnline(Long userId);
    void setUserOffline(Long userId);
    
    // Game session management
    void saveGameSession(String gameId, Map<String, Object> sessionData);
    Map<String, Object> getGameSession(String gameId);
    void updateGameSession(String gameId, String key, Object value);
    void updateGameStatus(String gameId, GameStatus status);
    void deleteGameSession(String gameId);
}