package org.nocturne.component;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProcessRegistry {

    private final Map<String, Process> sessionMap = new ConcurrentHashMap<>();

    public void addProcess(String userId, Process process) {
        sessionMap.put(userId, process);
    }

    public void removeProcess(String userId) {
        sessionMap.remove(userId);
    }

    public Process getProcess(String userId) {
        return sessionMap.get(userId);
    }
}
