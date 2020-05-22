package org.nocturne.component;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsSessionRegistry {

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public void addSession(String userId, WebSocketSession session) {
        sessionMap.put(userId, session);
    }

    public void removeSession(String userId) {
        sessionMap.remove(userId);
    }

    public WebSocketSession getSession(String userId) {
        return sessionMap.get(userId);
    }
}
