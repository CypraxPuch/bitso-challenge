package com.ledze.bitcoin.bitsochallenge.operation;

import com.ledze.bitcoin.bitsochallenge.client.Op;
import com.ledze.bitcoin.bitsochallenge.client.OrderBook;
import com.ledze.bitcoin.bitsochallenge.client.OrderBookClient;
import com.ledze.bitcoin.bitsochallenge.pojo.DiffOrder;
import com.ledze.bitcoin.bitsochallenge.service.DiffOrdersService;
import com.ledze.bitcoin.bitsochallenge.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class OrderBookOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookOperation.class);
    private static final String WSS_URL = "wss://ws.bitso.com";
    @Autowired
    private DiffOrdersService diffOrdersService;
    @Autowired
    private OrderBookClient orderBookClient;
    //private CopyOnWriteArrayList<DiffOrder> diffOrders = new CopyOnWriteArrayList<>();
    private OrderBook orderBookFull = null;

    public void init() {

        diffOrdersService.setUrl(WSS_URL);

        //se supone que aquí se encolan los mensajes de entrada del canal diff-orders
        diffOrdersService.setOnSucceeded(event -> {
            LOGGER.info(diffOrdersService.getValue());
        });

        diffOrdersService.setOnFailed(event ->
                LOGGER.error("Unable to subscribe to " + WSS_URL, diffOrdersService.getException())
        );

        diffOrdersService.restart();

    }

    @JmsListener(destination = "difforders.queue")
    public void receiveQueue(String text) {
        //LOGGER.info("queue: "+text);
        DiffOrder diffOrder = JsonUtil.json2DiffOrder(text);

        if( orderBookFull==null ) {
            LOGGER.info("Calling order book rest service");
            String orderBookJsonString = orderBookClient.getOrderBookList("btc_mxn", "false");
            orderBookFull = JsonUtil.jsonToOrderBook(orderBookJsonString);
            LOGGER.info("orderBookFull:\n" + orderBookFull);
        }

        if (diffOrder.getSequence()>0 && orderBookFull.getSequence() > diffOrder.getSequence()) {
            LOGGER.info("diffOrder: " + diffOrder);
            applyDiffOrder2FullOrderBookStruc(diffOrder);
            //actualiza número de secuencia.
            orderBookFull.setSequence(diffOrder.getSequence());
        }
    }

    private void applyDiffOrder2FullOrderBookStruc(DiffOrder diffOrder) {

        List<String> listOidBids = this.orderBookFull.getBids()
                .parallelStream()
                .map(Op::getOid)
                .collect(Collectors.toList());

        List<String> listOidAsks = this.orderBookFull.getAsks()
                .parallelStream()
                .map(Op::getOid)
                .collect(Collectors.toList());

        diffOrder.getPayload()
                .parallelStream()
                .forEach(d -> {
                    if(d.getStatus().equalsIgnoreCase("open")) {
                        if (listOidBids.contains(d.getOid())) {
                            for (Op o : orderBookFull.getBids()) {
                                if (o.getOid().equalsIgnoreCase(d.getOid())) {
                                    o.setAmount(d.getAmount());
                                    o.setPrice(d.getRate());
                                    LOGGER.info("bid updated on oid:"+o.getOid());
                                    break;
                                }
                            }
                            //LOGGER.info("orderBook updated BIDS on oid: "+d.getOid()+"\n"+orderBookFull);
                        } else if (listOidAsks.contains(d.getOid())) {
                            for (Op o : orderBookFull.getAsks()) {
                                if (o.getOid().equalsIgnoreCase(d.getOid())) {
                                    o.setAmount(d.getAmount());
                                    o.setPrice(d.getRate());
                                    LOGGER.info("ask updated on oid:"+o.getOid());
                                    break;
                                }
                            }
                            //LOGGER.info("orderBook updated ASKS on oid: "+d.getOid()+"\n"+orderBookFull);
                        }
                    }
                });
    }

    public OrderBook getOrderBookFull() {
        return this.orderBookFull;
    }
}
