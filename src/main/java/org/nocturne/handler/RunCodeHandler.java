package org.nocturne.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.nocturne.bean.CodeFile;
import org.nocturne.util.PathUtil;
import org.nocturne.util.SessionUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.*;

@Slf4j
@Component
public class RunCodeHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = SessionUtil.getUserIdFromWsSession(session);

        runCode(message, userId);
        log.info(String.format("[%s] start running code", userId));
    }

    private void runCode(TextMessage message, String userId) throws InterruptedException, IOException {
        CodeFile codeFile = getCodeFileFromInputJSON(message);

        createRunCodeFolderAndFile(codeFile, userId);
        doRunCode(codeFile, userId);
    }

    private CodeFile getCodeFileFromInputJSON(TextMessage message) {
        String json = message.getPayload();
        System.out.println(json);
        return JSON.parseObject(json, CodeFile.class);
    }

    private void createRunCodeFolderAndFile(CodeFile codeFile, String userId) {
        try {
            createCodeFolder(userId);
            createCodeFile(codeFile, userId);
            createPipeLine(userId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createCodeFolder(String userId) {
        File file = new File(PathUtil.getCodeFolderPath(userId));

        if (!file.mkdirs()) {
            log.error(String.format("[%s] folder create error!", userId));
        }
    }

    private void createCodeFile(CodeFile codeFile, String userId) throws IOException {
        File file = new File(PathUtil.getCodeFilePath(codeFile, userId));

        boolean ignore = file.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(codeFile.getCode());
        writer.close();
    }

    private void createPipeLine(String userId) throws IOException {
        File workDir = new File(PathUtil.getCodeFolderPath(userId));

        Runtime.getRuntime().exec(new String[]{"sh", "-c", "mkfifo input.pipe"}, null, workDir);
        Runtime.getRuntime().exec(new String[]{"sh", "-c", "mkfifo output.pipe"}, null, workDir);
    }

    private void doRunCode(CodeFile codeFile, String userId) throws InterruptedException, IOException {
        String folderPath = PathUtil.getCodeFolderPath(userId);

        Runtime runtime = Runtime.getRuntime();
        switch (codeFile.getType()) {
            case PYTHON: {
                runtime.exec(new String[]{"sh", "-c", "python main.py < input.pipe > output.pipe &"}, null, new File(folderPath));
                break;
            }
            case JAVA: {
            }
            case CPP: {
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }
}