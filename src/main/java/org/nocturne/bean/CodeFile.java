package org.nocturne.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeFile {
    public enum Type {
        PYTHON, CPP, JAVA;

        public String getFileSuffix() {
            switch (this) {
                case CPP: return ".cpp";
                case JAVA: return ".java";
                case PYTHON: return ".py";
                default: throw new IllegalArgumentException();
            }
        }
    }

    private Type type;
    private String code;
}
