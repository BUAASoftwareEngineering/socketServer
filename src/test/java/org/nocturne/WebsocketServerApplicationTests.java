package org.nocturne;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.nocturne.bean.CodeFile;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest
class WebsocketServerApplicationTests {

    @Test
    void contextLoads() throws Exception {
        File dir = new File("/home/nocturne/temp/123");
        Runtime.getRuntime().exec(new String[]{"sh", "-c", "python main.py < input.pipe > output.pipe &"}, null, dir).waitFor();

    }

}
