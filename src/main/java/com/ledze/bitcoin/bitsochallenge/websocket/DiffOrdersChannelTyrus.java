package com.ledze.bitcoin.bitsochallenge.websocket;

import javax.websocket.*;

import org.glassfish.tyrus.client.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DiffOrdersChannelTyrus {
    private static CountDownLatch messageLatch;
    private static final String SENT_MESSAGE = "{ action: 'subscribe', book: 'btc_mxn', type: 'diff-orders' }";

    public static void main(String[] args) {
        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = ClientManager.createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    System.out.println("onOpen...");
                    System.out.println("session ID: " + session.getId());
                    System.out.println("open: " + session.isOpen());
                    System.out.println("secure: " + session.isSecure());
                    try {
                        session.addMessageHandler(String.class, message -> {
                            System.out.println("Received message: " + message);
                            messageLatch.countDown();
                        });
                        System.out.println("waiting for 10sec...");
                        messageLatch.await(10, TimeUnit.SECONDS);
                        System.out.println("send message.");
                        session.getBasicRemote().sendText(SENT_MESSAGE);
                        System.out.println("message send succesfully");
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, cec, new URI("wss://ws.bitso.com"));
            messageLatch.await(10, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
