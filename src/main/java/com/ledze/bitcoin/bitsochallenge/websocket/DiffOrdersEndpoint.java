package com.ledze.bitcoin.bitsochallenge.websocket;

import com.ledze.bitcoin.bitsochallenge.configuration.StaticApplicationContext;
import com.ledze.bitcoin.bitsochallenge.jms.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.websocket.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ClientEndpoint
public class DiffOrdersEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrdersEndpoint.class);

    private String subscriptionMessage;
    private String response;
    private Throwable exception;
    private final CountDownLatch messageLatch = new CountDownLatch(1);
    private static final int REQUEST_TIMEOUT_SECS = 10;

    @OnOpen
    public void onOpen(Session session) {
        try {
            LOGGER.info("Sending request: '" + subscriptionMessage + "' with session " + session.getId());
            session.getBasicRemote().sendText(subscriptionMessage);
        } catch (IOException e) {
            LOGGER.error("Unable to connect to hello server: ", e);
        }
    }

    @OnMessage
    public void processResponse(Session session, String message) {
//        LOGGER.debug("Received response: '" + message + "' for request: '" + name + "' with session " + session.getId());
        //LOGGER.info(message);
        response = message;
        if( !message.contains("action") && !message.contains("response")) {
            ((Producer) StaticApplicationContext.getContext().getBean("producer")).send(message);
        }
        messageLatch.countDown();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("Communication error, subscription with '" + subscriptionMessage + "' with session " + session.getId(), throwable);
        exception = throwable;
        messageLatch.countDown();
    }

    /**
     * Blocks until either the server sends a response to the request, an communication error occurs
     * or the communication request times out.
     *
     * @return the server response message.
     * @throws TimeoutException     if the server does not respond before the timeout value is reached.
     * @throws InterruptedException if the communication thread is interrupted (e.g. thread.interrupt() is invoked on it for cancellation purposes).
     * @throws IOException          if a communication error occurs.
     */
    public String getResponse() throws TimeoutException, InterruptedException, IOException {
        if (messageLatch.await(REQUEST_TIMEOUT_SECS, TimeUnit.SECONDS)) {
            if (exception != null) {
                throw new IOException("Unable to get response", exception);
            }
            return response;
        } else {
            throw new TimeoutException("Timed out awaiting subscription response with " + subscriptionMessage);
        }
    }

    public String getSubscriptionMessage() {
        return subscriptionMessage;
    }

    public void setSubscriptionMessage(String subscriptionMessage) {
        this.subscriptionMessage = subscriptionMessage;
    }
}
