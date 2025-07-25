package com.jairath.websocket.poc.impl.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.jairath.websocket.poc.impl.service.Helper.getUserId;




/*
We can nOT store full WebSocketSession in a database
Because:
    WebSocketSession contains open TCP connection and thread-local context.
    It is not serializable and not shareable across nodes.

    What to do instead?
    You store lightweight user-session mappings in a shared store (e.g., Redis), like:
    "userId: a0j0e7t" -> ["sessionId1", "sessionId2"]
 */

@Component
@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {

    // List to store active sessions
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final Map<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

        String userId = getUserId(session);

        session.getAttributes().put("userId", userId);

        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessions.add(session); // Optional: global list

        session.sendMessage(new TextMessage("Connection established with server."));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received from client: " + payload);

        // Echo message back to that user
        session.sendMessage(new TextMessage("You said: " + payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            Set<WebSocketSession> userSessionSet = userSessions.get(userId);
            if (userSessionSet != null) {
                userSessionSet.remove(session);
                if (userSessionSet.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }
        sessions.remove(session); // optional global list
    }

    public void sendToUser(String userId, String message) {
        Set<WebSocketSession> userSessionSet = userSessions.get(userId);
        if (userSessionSet != null) {
            for (WebSocketSession session : userSessionSet) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.error("Exception occurred while sending event to userId: {}", userId, e);
                    }
                }
            }
        }
    }
}

