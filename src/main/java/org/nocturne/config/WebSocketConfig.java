package org.nocturne.config;

import org.nocturne.handler.RunCodeHandler;
import org.nocturne.interceptor.WsSessionHandleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "test")
                .addInterceptors(new WsSessionHandleInterceptor())
                .setAllowedOrigins("*");
    }

    public WebSocketHandler myHandler() {
        return new RunCodeHandler();
    }

}