package com.dguossp.santong.configuration;

import com.dguossp.santong.interceptor.stomp.TopicSubInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-stomp
 *
 * */

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private TopicSubInterceptor topicSubInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Use the built-in message broker for subscriptions and broadcasting and
        // route messages whose destination header begins with /topic `or `/queue to the broker.
        registry.enableSimpleBroker("/topic");

        // STOMP messages whose destination header begins with /app are routed to
        // @MessageMapping methods in @Controller classes.
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // HTTP URL for the endpoint to which a WebSocket
        // client needs to connect for the WebSocket handshake.
        registry.addEndpoint("/gs-guide-websocket").withSockJS();
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(topicSubInterceptor);
    }
}
