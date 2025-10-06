package com.example.quizizz.controller.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.quizizz.controller.socketio.listener.ConnectionHandler;
import com.example.quizizz.controller.socketio.listener.EventRegistrar;
import com.example.quizizz.controller.socketio.session.SessionManager;
import com.example.quizizz.repository.UserRepository;
import com.example.quizizz.security.JwtUtil;
import com.example.quizizz.service.Interface.IRoomService;
import com.example.quizizz.service.Interface.IGameService;
import com.example.quizizz.service.Interface.ISocketIOService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Getter
public class SocketIOEventHandler {

    private final SocketIOServer socketIOServer;
    private final JwtUtil jwtUtil;
    private final IRoomService roomService;
    private final IGameService gameService;
    private final ISocketIOService socketIOService;
    private final SessionManager sessionManager;
    private final ConnectionHandler connectionHandler;
    private final EventRegistrar eventRegistrar;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        connectionHandler.registerListeners(socketIOServer);
        eventRegistrar.registerEvents(socketIOServer, this);
        log.info("Socket.IO server initialized successfully");
    }
}