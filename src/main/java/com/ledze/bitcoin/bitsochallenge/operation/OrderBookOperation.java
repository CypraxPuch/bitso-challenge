package com.ledze.bitcoin.bitsochallenge.operation;

import com.ledze.bitcoin.bitsochallenge.client.OrderBookClient;
import com.ledze.bitcoin.bitsochallenge.pojo.DiffOrder;
import com.ledze.bitcoin.bitsochallenge.service.DiffOrdersService;
import com.ledze.bitcoin.bitsochallenge.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class OrderBookOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookOperation.class);
    private static final String WSS_URL = "wss://ws.bitso.com";
    @Autowired private DiffOrdersService diffOrdersService;
    @Autowired private OrderBookClient orderBookClient;
    private CopyOnWriteArrayList<DiffOrder> diffOrders = new CopyOnWriteArrayList<>();

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

        validateOrderBookAgainstDiffOrders();

    }

    @JmsListener(destination = "difforders.queue")
    public void receiveQueue(String text) {
        //LOGGER.info(text);
        diffOrders.add(JsonUtil.json2DiffOrder(text));
    }

    private void validateOrderBookAgainstDiffOrders(){
        LOGGER.info("validateOrderBookAgainstDiffOrders");
        diffOrders.forEach(o -> LOGGER.info(o.toString()));
    }
}
