package com.jairath.websocket.poc.controller;

import com.jairath.websocket.poc.impl.service.MyWebSocketHandler;
import com.jairath.websocket.poc.impl.service.WebSocketHandlerWithRedis;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.jairath.websocket.poc.impl.service.Constants.REDIS_TOPIC_KEY_PREFIX;


@RestController
@Scope
@Slf4j
@Validated
@CrossOrigin
@RequestMapping(value = "/jairath/web-sockets")
@Api
public class WebSocketTestController {

    private final MyWebSocketHandler handler;

    private final WebSocketHandlerWithRedis webSocketHandlerWithRedis;
    private final StringRedisTemplate redis;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public WebSocketTestController(MyWebSocketHandler handler,
                                   WebSocketHandlerWithRedis webSocketHandlerWithRedis,
                                   StringRedisTemplate redis,
                                   RedisTemplate<String, String> redisTemplate) {
        this.handler = handler;
        this.webSocketHandlerWithRedis = webSocketHandlerWithRedis;
        this.redis = redis;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessageToUser(@RequestParam String userId,
                                                    @RequestParam String message) {
        //webSocketHandlerWithRedis.sendToUser(userId, message);
        //redis.convertAndSend(REDIS_TOPIC_KEY_PREFIX + userId, message);
        redisTemplate.convertAndSend(REDIS_TOPIC_KEY_PREFIX + userId, message);
        return ResponseEntity.ok("Message sent");
    }
}
