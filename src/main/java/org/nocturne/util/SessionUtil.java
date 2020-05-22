package org.nocturne.util;

import org.springframework.web.socket.WebSocketSession;

public class SessionUtil {
    public static String getUserIdFromWsSession(WebSocketSession session) {
        return session.getAttributes().get("userId").toString();
    }
}
