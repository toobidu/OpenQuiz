package com.example.quizizz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public String getSecretKey() {
        return secretKey;
    }

    public Long getAccessExpiration() {
        return accessExpiration;
    }

    public Long getRefreshExpiration() {
        return refreshExpiration;
    }
}