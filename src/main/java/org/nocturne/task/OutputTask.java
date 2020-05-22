package org.nocturne.task;

import lombok.SneakyThrows;
import org.nocturne.util.PathUtil;
import org.nocturne.util.SessionUtil;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class OutputTask implements Runnable {

    private final WebSocketSession session;

    public OutputTask(WebSocketSession session) {
        this.session = session;
    }

    @SneakyThrows
    @Override
    public void run() {
        String userId = SessionUtil.getUserIdFromWsSession(session);
        BufferedWriter blocker = new BufferedWriter(new FileWriter(PathUtil.getCodeFolderPath(userId) + "/input.pipe"));

        BufferedReader reader = new BufferedReader(new FileReader(PathUtil.getCodeFolderPath(userId) + "/output.pipe"));
        while (session.isOpen()) {
            if (!reader.ready()) {
                Thread.sleep(1000);
                continue;
            }

            while (reader.ready()) {
                String line = reader.readLine();
                if (line == null) {
                    session.close();
                } else {
                    session.sendMessage(new TextMessage(line + "\n"));
                }
            }
        }

        reader.close();
        blocker.close();
    }
}
