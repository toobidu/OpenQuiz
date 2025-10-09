package com.example.quizizz.service.Implement;

import com.example.quizizz.common.constants.GameStatus;
import com.example.quizizz.common.constants.PermissionCode;
import com.example.quizizz.service.Interface.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImplement implements IRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void blacklistToken(String token, long expirationTime) {
        String key = "token:blacklist:" + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = "token:blacklist:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void cacheUserPermissions(Long userId, Set<PermissionCode> permissions) {
        String key = "user:" + userId + ":permissions";
        redisTemplate.opsForValue().set(key, permissions, 1, TimeUnit.HOURS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<PermissionCode> getUserPermissions(Long userId) {
        String key = "user:" + userId + ":permissions";
        return (Set<PermissionCode>) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void clearUserPermissions(Long userId) {
        String key = "user:" + userId + ":permissions";
        redisTemplate.delete(key);
    }

    @Override
    public void saveGameSession(String gameId, Map<String, Object> sessionData) {
        redisTemplate.opsForHash().putAll(gameId, sessionData);
        redisTemplate.expire(gameId, 2, TimeUnit.HOURS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getGameSession(String gameId) {
        Map<Object, Object> rawData = redisTemplate.opsForHash().entries(gameId);
        if (rawData.isEmpty()) {
            return null;
        }
        
        Map<String, Object> sessionData = new HashMap<>();
        rawData.forEach((key, value) -> sessionData.put(key.toString(), value));
        return sessionData;
    }

    @Override
    public void updateGameSession(String gameId, String key, Object value) {
        redisTemplate.opsForHash().put(gameId, key, value);
    }

    @Override
    public void updateGameStatus(String gameId, GameStatus status) {
        redisTemplate.opsForHash().put(gameId, "status", status.name());
    }

    @Override
    public void deleteGameSession(String gameId) {
        redisTemplate.delete(gameId);
    }

    @Override
    public void addTokenToBlacklistWithRefreshTTL(String token, long ttl) {
        String key = "token:blacklist:" + token;
        redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public void saveUserPermissions(Long userId, Set<PermissionCode> permissions) {
        cacheUserPermissions(userId, permissions);
    }

    @Override
    public void deleteUserPermissionsCache(Long userId) {
        clearUserPermissions(userId);
    }

    @Override
    public void setUserOnline(Long userId) {
        String key = "user:" + userId + ":online";
        redisTemplate.opsForValue().set(key, "true", 24, TimeUnit.HOURS);
    }

    @Override
    public void setUserOffline(Long userId) {
        String key = "user:" + userId + ":online";
        redisTemplate.delete(key);
    }
}