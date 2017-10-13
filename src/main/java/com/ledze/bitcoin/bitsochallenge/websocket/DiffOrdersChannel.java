package com.ledze.bitcoin.bitsochallenge.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Configuration
public class DiffOrdersChannel {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrdersChannel.class);
    private static Object waitLock = new Object();

    private static final String WS_URI = "wss://ws.bitso.com";

    public void init(){
        try {
            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ClientConfig.class);
            LOGGER.info("\n\n\nWhen ready, press any key to exit\n\n\n");
            System.in.read();
            ctx.close();
        } catch (Throwable t) {
            LOGGER.error("error ",t);
        } finally {
            System.exit(0);
        }

    }

    @Configuration
    static class ClientConfig {

        @Bean
        public WebSocketConnectionManager connectionManager() {
            WebSocketConnectionManager manager = new WebSocketConnectionManager(client(), handler(), WS_URI);
            manager.setAutoStartup(true);
            return manager;
        }

        @Bean
        public StandardWebSocketClient client() {
            return new StandardWebSocketClient();
        }

        @Bean
        public TextWebSocketHandler handler() {
            return new TextWebSocketHandler() {
                @Override
                protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                    LOGGER.info(message.getPayload());
                }
            };
        }

    }
}
