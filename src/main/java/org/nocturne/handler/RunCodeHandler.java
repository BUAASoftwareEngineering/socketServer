package org.nocturne.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.nocturne.bean.CodeFile;
import org.nocturne.component.ProcessRegistry;
import org.nocturne.util.PathUtil;
import org.nocturne.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.util.FileSystemUtils;

import java.io.*;

@Slf4j
@Component
public class RunCodeHandler extends TextWebSocketHandler {

    private final ProcessRegistry processRegistry;

    @Autowired
    public RunCodeHandler(ProcessRegistry processRegistry) {
        this.processRegistry = processRegistry;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = SessionUtil.getUserIdFromWsSession(session);

        Process process = runCode(message, userId);
        this.processRegistry.addProcess(userId, process);

        log.info(String.format("[%s] start running code", userId));
    }

    private Process runCode(TextMessage message, String userId) throws InterruptedException, IOException {
        CodeFile codeFile = getCodeFileFromInputJSON(message);

        deleteOldFolder(userId);
        createRunCodeFolderAndFile(codeFile, userId);
        return doRunCode(codeFile, userId);
    }

    private void deleteOldFolder(String userId) {
        File codeFolder = new File(PathUtil.getCodeFolderPath(userId));
        FileSystemUtils.deleteRecursively(codeFolder);
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

    private Process doRunCode(CodeFile codeFile, String userId) throws InterruptedException, IOException {
        String folderPath = PathUtil.getCodeFolderPath(userId);

        Runtime runtime = Runtime.getRuntime();
        switch (codeFile.getType()) {
            case PYTHON: {
                return runtime.exec(new String[]{"sh", "-c", "python main.py < input.pipe > output.pipe 2>&1"}, null, new File(folderPath));
            }
            case CPP: {
		runtime.exec(new String[]{"sh", "-c", "g++ main.cpp"}, null, new File(folderPath)).waitFor();
		return runtime.exec(new String[]{"sh", "-c", "./a.out < input.pipe > output.pipe 2>&1"}, null, new File(folderPath));
            }
            case JAVA: {
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }
}
