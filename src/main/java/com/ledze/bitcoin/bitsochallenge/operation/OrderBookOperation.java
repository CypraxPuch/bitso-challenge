package com.ledze.bitcoin.bitsochallenge.operation;

import com.ledze.bitcoin.bitsochallenge.client.OrderBookClient;
import com.ledze.bitcoin.bitsochallenge.service.DiffOrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderBookOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookOperation.class);
    private static final String WSS_URL = "wss://ws.bitso.com";
    @Autowired private DiffOrdersService diffOrdersService;
    @Autowired private OrderBookClient orderBookClient;

    public void init(){

        diffOrdersService.setUrl(WSS_URL);

        //se supone que aquí se encolan los mensajes de entrada del canal diff-orders
        diffOrdersService.setOnSucceeded(event -> {
            LOGGER.info(diffOrdersService.getValue());
        });

        diffOrdersService.setOnFailed(event ->
                LOGGER.error("Unable to subscribe to " + WSS_URL, diffOrdersService.getException())
        );

        diffOrdersService.restart();

        LOGGER.info("Calling order book rest service");
        String orderBookJsonString = orderBookClient.getOrderBookList("btc_mxn", "false");

    }
}
