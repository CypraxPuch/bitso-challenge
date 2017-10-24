package com.ledze.bitcoin.bitsochallenge.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Scanner;

@Component
public class DiffOrdersChannel {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrdersChannel.class);

    private static final String WS_URI = "wss://ws.bitso.com";
    @Autowired private ChallengeSessionHandler stompSessionHandler;

    public void init() {
        LOGGER.info("init DiffOrdersChannel...");
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        ListenableFuture<StompSession> connection = stompClient.connect(WS_URI, stompSessionHandler);

        new Scanner(System.in).nextLine();
    }

}

@Component
class ChallengeSessionHandler extends StompSessionHandlerAdapter {


    private Logger logger = LoggerFactory.getLogger(ChallengeSessionHandler.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
        session.subscribe("/subscribe", this);
        logger.info("Subscribed to /subscribe");
        session.send("", "{ action: 'subscribe', book: 'btc_mxn', type: 'diff-orders' }");
        logger.info("Message sent to websocket server");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.info("Received : " + payload);
    }

}

