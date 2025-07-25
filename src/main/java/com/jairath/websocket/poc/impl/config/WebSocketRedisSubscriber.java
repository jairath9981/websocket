package com.jairath.websocket.poc.impl.config;

import com.jairath.websocket.poc.impl.service.WebSocketHandlerWithRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import static com.jairath.websocket.poc.impl.service.Constants.REDIS_TOPIC_KEY_PREFIX;

@Component
public class WebSocketRedisSubscriber {

/*
    below two beans and this @Autowired is used when we are going without wildcard topic.

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {}

    @Bean
    public MessageListenerAdapter listenerAdapter(WebSocketHandlerWithRedis handler) {}

        @Autowired
    public WebSocketRedisSubscriber(WebSocketHandlerWithRedis handler) {}

 */

//    private final WebSocketHandlerWithRedis handler;
//
//    @Autowired
//    public WebSocketRedisSubscriber(WebSocketHandlerWithRedis handler) {
//        this.handler = handler;
//    }
//
//    @Bean
//    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
//                                                   MessageListenerAdapter listenerAdapter) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.addMessageListener(listenerAdapter, new PatternTopic(REDIS_TOPIC_KEY_PREFIX+"a0j0e7t"));
//        return container;
//    }
//
//    @Bean
//    public MessageListenerAdapter listenerAdapter(WebSocketHandlerWithRedis handler) {
//        return new MessageListenerAdapter(handler, "onMessageFromRedis");
//    }


    /*

       public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   WebSocketHandlerWithRedis handler) {}

       This is the only Bean we required when we need to go with wildcard topic.

     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   WebSocketHandlerWithRedis handler) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new MessageListenerAdapter(handler), new PatternTopic(REDIS_TOPIC_KEY_PREFIX+"*"));
        return container;
    }
}
