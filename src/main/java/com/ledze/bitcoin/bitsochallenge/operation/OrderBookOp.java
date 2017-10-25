package com.ledze.bitcoin.bitsochallenge.operation;

import com.ledze.bitcoin.bitsochallenge.client.OrderBookClient;
import com.ledze.bitcoin.bitsochallenge.websocket.DiffOrdersChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderBookOp {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookOp.class);
    @Autowired private OrderBookClient orderBookClient;
    @Autowired private DiffOrdersChannel diffOrdersChannel;

    public void init(){

        LOGGER.info("Subscribing and getting diff-orders info");
        diffOrdersChannel.init();

        LOGGER.info("calling order book rest service");
        orderBookClient.getOrderBookList("btc_mxn", "false");
    }

}
