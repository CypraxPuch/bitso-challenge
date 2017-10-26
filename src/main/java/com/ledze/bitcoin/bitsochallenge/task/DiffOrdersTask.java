package com.ledze.bitcoin.bitsochallenge.task;

import com.ledze.bitcoin.bitsochallenge.websocket.DiffOrdersEndpoint;
import javafx.concurrent.Task;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;

public class DiffOrdersTask extends Task<String>{
    //private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrdersTask.class);
    private final String subscriptionMessage;
    private final String serverEndpointUrl;

    public DiffOrdersTask(String subscriptionMsg, String url) {
        this.subscriptionMessage = subscriptionMsg;
        this.serverEndpointUrl = url;
    }

    @Override
    protected String call() throws Exception {
        String response = null;

        DiffOrdersEndpoint clientEndpoint = new DiffOrdersEndpoint( this.subscriptionMessage );

        try {
            ClientManager client = ClientManager.createClient();
            client.connectToServer(
                    clientEndpoint,
                    URI.create(this.serverEndpointUrl)
            );

            response = clientEndpoint.getResponse();
        } catch (DeploymentException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        return response;
    }
}
