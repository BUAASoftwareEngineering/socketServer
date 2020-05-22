package org.nocturne.config;

import org.nocturne.handler.IOHandler;
import org.nocturne.handler.RunCodeHandler;
import org.nocturne.interceptor.WsSessionHandleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RunCodeHandler runCodeHandler;
    private final IOHandler ioHandler;

    private final WsSessionHandleInterceptor wsSessionHandleInterceptor;

    @Autowired
    public WebSocketConfig(RunCodeHandler runCodeHandler,
                           IOHandler ioHandler,
                           WsSessionHandleInterceptor wsSessionHandleInterceptor) {
        this.runCodeHandler = runCodeHandler;
        this.ioHandler = ioHandler;

        this.wsSessionHandleInterceptor = wsSessionHandleInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(runCodeHandler, "run")
                .addInterceptors(wsSessionHandleInterceptor)
                .setAllowedOrigins("*");

        registry.addHandler(ioHandler, "io")
                .addInterceptors(wsSessionHandleInterceptor)
                .setAllowedOrigins("*");
    }

}