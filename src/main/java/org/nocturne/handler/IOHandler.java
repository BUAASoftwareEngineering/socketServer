package org.nocturne.handler;

import lombok.extern.slf4j.Slf4j;
import org.nocturne.component.OutputThreadPool;
import org.nocturne.component.ProcessRegistry;
import org.nocturne.component.WsSessionRegistry;
import org.nocturne.task.OutputTask;
import org.nocturne.util.PathUtil;
import org.nocturne.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@Slf4j
@Component
public class IOHandler extends TextWebSocketHandler {

    private final WsSessionRegistry sessionRegistry;
    private final ProcessRegistry processRegistry;
    private final OutputThreadPool threadPool;

    @Autowired
    public IOHandler(WsSessionRegistry sessionRegistry,
                     ProcessRegistry processRegistry,
                     OutputThreadPool threadPool) {
        this.sessionRegistry = sessionRegistry;
        this.processRegistry = processRegistry;
        this.threadPool = threadPool;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // wait one second for client to add listener
        Thread.sleep(1000);

        // save session in registry
        String userId = SessionUtil.getUserIdFromWsSession(session);
        sessionRegistry.addSession(userId, session);

        // start a thread to send output back to browser
        threadPool.execTask(new OutputTask(session, processRegistry));

        log.info("start io thread successfully");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = SessionUtil.getUserIdFromWsSession(session);

        // remove saved session and delete generated files
        sessionRegistry.removeSession(userId);
        processRegistry.removeProcess(userId);
        deleteCodeFolder(userId);

        log.info("clean temp folder successfully");
    }

    private void deleteCodeFolder(String userId) {
        File codeFolder = new File(PathUtil.getCodeFolderPath(userId));
        FileSystemUtils.deleteRecursively(codeFolder);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = SessionUtil.getUserIdFromWsSession(session);

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(PathUtil.getCodeFolderPath(userId) + "/input.pipe")));
        writer.write(message.getPayload());
        writer.close();

        log.info(String.format("[%s] append input successfully", userId));
    }
}
