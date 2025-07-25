package com.jairath.websocket.poc.impl.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Set;

import static com.jairath.websocket.poc.impl.service.Constants.REDIS_TOPIC_KEY_PREFIX;
import static com.jairath.websocket.poc.impl.service.Helper.getUserId;


@Component
@Slf4j
public class WebSocketHandlerWithRedis  extends TextWebSocketHandler
        implements MessageListener
{

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final String instanceId = UUID.randomUUID().toString();

    @Autowired
    private StringRedisTemplate redis;

    private String keyPrefix = "ws:sessions:";


    @Autowired
    public WebSocketHandlerWithRedis(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        String userId = getUserId(webSocketSession);
        String sessionKey = instanceId + ":" + webSocketSession.getId();

        sessions.put(sessionKey, webSocketSession);
        redis.opsForSet().add(keyPrefix + userId, sessionKey);
        webSocketSession.getAttributes().put("sessionKey", sessionKey);
        webSocketSession.getAttributes().put("userId", userId);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException {
        session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String key = (String) session.getAttributes().get("sessionKey");
        String userId = (String) session.getAttributes().get("userId");

        sessions.remove(key);
        redis.opsForSet().remove(keyPrefix + userId, key);
    }

    public void sendToUser(String userId, String msg) {
        Set<String> keys = redis.opsForSet().members(keyPrefix + userId);
        for (String sessionKey : keys) {
            if (sessions.containsKey(sessionKey)) {
                try {
                    sessions.get(sessionKey)
                            .sendMessage(new TextMessage(msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
/*
    either use  public void onMessageFromRedis(String message, String channel) {}
    OR
    public void onMessage(Message message, byte[] pattern){}
    But, onMessage is more generic and can be used for wildCard matching. To use this we need to
    implements MessageListener.
    Both, requires different set of configurations in WebSocketRedisSubscriber.java
 */
//    public void onMessageFromRedis(String message, String channel) {
//        log.info("📥 Received message: " + message);
//
//        String userId = channel.split(REDIS_TOPIC_KEY_PREFIX)[1];
//        sendToUser(userId, message); // same logic as above
//    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("📥 Received from Redis: channel, payload");
        String channel = new String(message.getChannel());
        String payload = new String(message.getBody());

        String userId = channel.replaceFirst(REDIS_TOPIC_KEY_PREFIX, "");
        sendToUser(userId, payload);
    }
}
