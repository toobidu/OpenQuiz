package com.example.quizizz.common.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SocketIOServerRunner implements CommandLineRunner {

    private final SocketIOServer socketIOServer;

    @Override
    public void run(String... args) {
        try {
            socketIOServer.start();
            log.info("Socket.IO server started successfully on port: {}", socketIOServer.getConfiguration().getPort());
        } catch (Exception e) {
            log.error("Failed to start Socket.IO server: {}", e.getMessage());
        }
    }
}