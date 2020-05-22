package org.nocturne;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.nocturne.bean.CodeFile;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;

import java.io.*;

@SpringBootTest
class WebsocketServerApplicationTests {

    @Test
    void contextLoads() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(new File("/home/nocturne/temp/p.pipe")));

        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null) {
                System.out.println("1111111111111");
                break;
            }
            System.out.println(line);
        }

        String s = reader.readLine();
        System.out.println(s);
    }

}
