package com.ledze.bitcoin.bitsochallenge.task;

import com.ledze.bitcoin.bitsochallenge.websocket.DiffOrdersEndpoint;
import javafx.concurrent.Task;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;

@Component
public class DiffOrdersTask extends Task<String>{
    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrdersTask.class);
    private String subscriptionMessage;
    private String serverEndpointUrl;

    private DiffOrdersEndpoint clientEndpoint = new DiffOrdersEndpoint();

    @Override
    protected String call() throws Exception {
        String response = null;

        clientEndpoint.setSubscriptionMessage( this.subscriptionMessage );

        try {
            ClientManager client = ClientManager.createClient();
            client.connectToServer(
                    clientEndpoint,
                    URI.create(this.serverEndpointUrl)
            );

            response = clientEndpoint.getResponse();
            //updateValue(getValue()+"\n");
        } catch (DeploymentException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        LOGGER.info("response: "+response);
        return response;
    }

    public String getSubscriptionMessage() {
        return subscriptionMessage;
    }

    public void setSubscriptionMessage(String subscriptionMessage) {
        this.subscriptionMessage = subscriptionMessage;
    }

    public String getServerEndpointUrl() {
        return serverEndpointUrl;
    }

    public void setServerEndpointUrl(String serverEndpointUrl) {
        this.serverEndpointUrl = serverEndpointUrl;
    }
}
