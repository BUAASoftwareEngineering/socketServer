package org.nocturne.util;

import org.nocturne.bean.CodeFile;

public class PathUtil {

    public static String getCodeFilePath(CodeFile codeFile, String userId) {
        return "/home/nocturne/temp/" + userId + "/main" + codeFile.getType().getFileSuffix();
    }

    public static String getCodeFolderPath(String userId) {
        return "/home/nocturne/temp/" + userId;
    }
}
