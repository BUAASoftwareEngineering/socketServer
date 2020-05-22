package org.nocturne;

import org.nocturne.handler.MyHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import java.io.IOException;

@SpringBootApplication
public class WebsocketServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketServerApplication.class, args);

        new Thread(() -> {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                MyHandler.set.iterator().next().sendMessage(new TextMessage("hackkkkkkkkkkkkkkkk!"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
