package org.nocturne.task;

import lombok.SneakyThrows;
import org.nocturne.component.ProcessRegistry;
import org.nocturne.util.PathUtil;
import org.nocturne.util.SessionUtil;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class OutputTask implements Runnable {

    private static final int SLEEP_THRESHOLD = 40;

    private final WebSocketSession session;
    private final Process process;

    public OutputTask(WebSocketSession session, ProcessRegistry processRegistry) {
        this.session = session;
        this.process = processRegistry.getProcess(SessionUtil.getUserIdFromWsSession(session));
    }

    @SneakyThrows
    @Override
    public void run() {
        int sleepCount = 0;

        String userId = SessionUtil.getUserIdFromWsSession(session);
        BufferedWriter blocker = new BufferedWriter(new FileWriter(PathUtil.getCodeFolderPath(userId) + "/input.pipe"));

        BufferedReader reader = new BufferedReader(new FileReader(PathUtil.getCodeFolderPath(userId) + "/output.pipe"));
        while (session.isOpen() && sleepCount < SLEEP_THRESHOLD) {
            if (!process.isAlive() && !reader.ready()) break;

            if (!reader.ready()) {
                Thread.sleep(1000);
                sleepCount++;
                continue;
            }

            while (reader.ready()) {
                String line = reader.readLine();
                session.sendMessage(new TextMessage(line + "\n"));
            }
        }

        process.destroy();
        session.close();
        reader.close();
        blocker.close();
    }
}
