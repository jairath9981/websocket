package com.jairath.websocket.poc.impl.service;

import org.springframework.web.socket.WebSocketSession;

public class Helper {

    public static String getUserId(WebSocketSession session) {
        String query = session.getUri().getQuery(); // example: userId=a0j0e7t
        String userId = query != null && query.startsWith("userId=")
                ? query.substring(7) : "anonymous";

        return userId;
    }
}
