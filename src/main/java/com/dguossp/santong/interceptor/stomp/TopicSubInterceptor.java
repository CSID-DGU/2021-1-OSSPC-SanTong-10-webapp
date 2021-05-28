package com.dguossp.santong.interceptor.stomp;

import com.dguossp.santong.entity.redis.RedisGameSearch;
import com.dguossp.santong.repository.redis.RedisGameSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TopicSubInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = stompHeaderAccessor.getCommand();
        log.info("SessionID : " + message.getHeaders().get("simpSessionId").toString());


        if (StompCommand.CONNECT.equals(command)) {
            log.info("[STOMP] CONN");
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            // 1. [게임 찾기] 버튼 클릭 시, "____" 목적지 구독 요청
            // 2. Redis Server에 게임 찾기 요청 보낸 유저의 상태 업데이트
            log.info("[STOMP] SUB");
            log.info("STOMP HEADER : " + stompHeaderAccessor.getNativeHeader("test").get(0));
        }

        return message;
    }
}
