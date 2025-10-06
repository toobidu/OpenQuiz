package com.example.quizizz.common.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SocketIOConfig {

    @Value("${socket.io.host:localhost}")
    private String host;

    @Value("${socket.io.port:9092}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);

        // CORS configuration for multiple origins
        config.setOrigin("*");

        // Connection configuration
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);
        config.setRandomSession(true);

        // Timeouts for real-time gaming
        config.setPingTimeout(60000);
        config.setPingInterval(25000);

        // Authentication
        config.setAuthorizationListener(data -> {
            String token = data.getSingleUrlParam("token");
            return new com.corundumstudio.socketio.AuthorizationResult(token != null && !token.isEmpty());
        });

        // Configure Jackson to handle Java 8 date/time types (LocalDateTime, etc.)
        config.setJsonSupport(new com.corundumstudio.socketio.protocol.JacksonJsonSupport(new JavaTimeModule()));

        log.info("Socket.IO server configured on {}:{}", host, port);

        return new SocketIOServer(config);
    }
}