package com.dguossp.santong.controller;

import com.dguossp.santong.dto.request.Greeting;
import com.dguossp.santong.dto.request.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay

        // https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-stomp-handle-annotations
        // is serialized to a payload through a matching MessageConverter
        // and sent as a Message to the brokerChannel, from where it is broadcast to subscribers.
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

}