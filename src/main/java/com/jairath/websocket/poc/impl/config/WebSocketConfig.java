package com.jairath.websocket.poc.impl.config;


import com.jairath.websocket.poc.impl.service.MyWebSocketHandler;
import com.jairath.websocket.poc.impl.service.WebSocketHandlerWithRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MyWebSocketHandler myWebSocketHandler;
    private final WebSocketHandlerWithRedis webSocketHandlerWithRedis;

    @Autowired
    public WebSocketConfig(MyWebSocketHandler myWebSocketHandler,
                           WebSocketHandlerWithRedis webSocketHandlerWithRedis) {
        this.myWebSocketHandler = myWebSocketHandler;
        this.webSocketHandlerWithRedis = webSocketHandlerWithRedis;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(webSocketHandlerWithRedis, "/ws")
                .setAllowedOrigins("*"); // For local dev only
    }
}
