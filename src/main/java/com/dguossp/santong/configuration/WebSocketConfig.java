package com.dguossp.santong.configuration;

import lombok.extern.slf4j.Slf4j;
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
        registration.interceptors(new ChannelInterceptor() {
            /**
             * Invoked before the Message is actually sent to the channel.
             * This allows for modification of the Message if necessary.
             * If this method returns {@code null} then the actual
             * send invocation will not occur.
             *
             * @param message
             * @param channel
             */
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
                StompCommand command = sha.getCommand();

                if (StompCommand.CONNECT.equals(command)) {
                    log.info("{}, message.getPayload {}", command, message.getPayload());
                    log.info("{}, message.getHeader {}", command, message.getHeaders());
                    log.info("{}, accessor.toString {}", command, sha.toString());
                    log.info("{}, accessor.getDestination()", sha.getDestination());

                } else if(StompCommand.UNSUBSCRIBE.equals(command) && message.getHeaders().get("simpSubscriptionId").toString().startsWith("room-user-")) {
                    String sessionId = message.getHeaders().get("simpSessionId").toString();
                    log.info("sessionId : {}", sessionId);
                } else if(StompCommand.DISCONNECT.equals(command)) { // 갑작스러운 종료에도 채팅창을 잘 나가게 해야한다.
                    String sessionId = message.getHeaders().get("simpSessionId").toString();
                    log.info("sessionId : {}", sessionId);
                }
                return message;
            }
        });
    }
}
