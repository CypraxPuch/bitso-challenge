package com.ledze.bitcoin.bitsochallenge.websocket;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class DiffOrdersChannelTyrus {
    private static CountDownLatch messageLatch;
    private static final String SENT_MESSAGE = "{\"action\":\"subscribe\",\"book\":\"btc_mxn\",\"type\":\"diff-orders\"}";

    public static void main(String[] args) {
        try {

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
                        });
                        System.out.println("send message.");
                        session.getBasicRemote().sendText(SENT_MESSAGE);
                        System.out.println("message send succesfully");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, cec, new URI("wss://ws.bitso.com"));
            
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
