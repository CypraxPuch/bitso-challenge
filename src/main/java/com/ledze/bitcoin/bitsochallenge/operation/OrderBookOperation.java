package com.ledze.bitcoin.bitsochallenge.operation;

import com.ledze.bitcoin.bitsochallenge.client.Op;
import com.ledze.bitcoin.bitsochallenge.client.OrderBook;
import com.ledze.bitcoin.bitsochallenge.client.OrderBookClient;
import com.ledze.bitcoin.bitsochallenge.pojo.DiffOrder;
import com.ledze.bitcoin.bitsochallenge.service.DiffOrdersService;
import com.ledze.bitcoin.bitsochallenge.util.JsonUtil;
import javafx.util.converter.BigDecimalStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class OrderBookOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookOperation.class);
    private static final String WSS_URL = "wss://ws.bitso.com";
    @Autowired
    private DiffOrdersService diffOrdersService;
    @Autowired
    private OrderBookClient orderBookClient;
    private OrderBook orderBookFull = null;

    public void init() {

        diffOrdersService.setUrl(WSS_URL);

        //aquí se encolan los mensajes de entrada del canal diff-orders
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

        //LOGGER.info("DO-seq: "+diffOrder.getSequence()+" OB-seq: "+orderBookFull.getSequence());
        if (diffOrder.getSequence()>0 && diffOrder.getSequence() > orderBookFull.getSequence()) {
            applyDiffOrder2FullOrderBookStruct(diffOrder);
        }
    }

    private void applyDiffOrder2FullOrderBookStruct(DiffOrder diffOrder) {

        List<String> listOidBids = getAllOidsFromOrderBookFull(this.orderBookFull.getBids());
        List<String> listOidAsks = getAllOidsFromOrderBookFull(this.orderBookFull.getAsks());

        diffOrder.getPayload()
                .parallelStream()
                .forEach(d -> {
                    if( d.getAmount()==null || d.getAmount().equalsIgnoreCase(StringUtils.EMPTY) ) {
                        if (listOidBids.contains(d.getOid())) {
                            for (int x = 0; x < orderBookFull.getBids().size() ; x++){
                                Op o = orderBookFull.getBids().get(x);
                                if(d.getOid().equalsIgnoreCase(o.getOid())) {
                                    int prevSize = orderBookFull.getBids().size();
                                    orderBookFull.getBids().remove(o);
                                    LOGGER.info("(BID) elimina. antes: "+prevSize+" curr size: "+orderBookFull.getBids().size()+" orden:"+o.getOid() );
                                }
                            }
                        } else if (listOidAsks.contains(d.getOid())) {
                            for (int x = 0; x < orderBookFull.getAsks().size() ; x++){
                                Op o = orderBookFull.getAsks().get(x);
                                if(d.getOid().equalsIgnoreCase(o.getOid())) {
                                    int prevSize = orderBookFull.getAsks().size();
                                    orderBookFull.getAsks().remove(o);
                                    LOGGER.info("(ASK) elimina. antes: "+prevSize+" curr size: "+orderBookFull.getAsks().size()+" orden:"+o.getOid() );
                                }
                            }
                        }
                    } else if(d.getStatus().equalsIgnoreCase("open")) {
                        if (listOidBids.contains(d.getOid())) {
                            for (Op o : orderBookFull.getBids()) {
                                if (o.getOid().equalsIgnoreCase(d.getOid())) {
                                    o.setAmount(d.getAmount());
                                    o.setPrice(d.getRate());
                                    LOGGER.info("BID updated with:"+diffOrder);
                                    break;
                                }
                            }
                            //LOGGER.info("orderBook updated BIDS on oid: "+d.getOid()+"\n"+orderBookFull);
                        } else if (listOidAsks.contains(d.getOid())) {
                            for (Op o : orderBookFull.getAsks()) {
                                if (o.getOid().equalsIgnoreCase(d.getOid())) {
                                    o.setAmount(d.getAmount());
                                    o.setPrice(d.getRate());
                                    LOGGER.info("ASK updated with:"+diffOrder);
                                    break;
                                }
                            }
                            //LOGGER.info("orderBook updated ASKS on oid: "+d.getOid()+"\n"+orderBookFull);
                        } else {
                            //es una nueva orden... hay que agregarla al order book
                            Op operacionBidAsk = new Op();
                            operacionBidAsk.setAmount(d.getAmount());
                            operacionBidAsk.setBook("btc_mxn");
                            operacionBidAsk.setOid(d.getOid());
                            operacionBidAsk.setPrice(d.getRate());

                            if(d.getTypeBuySell()==0) {
                                orderBookFull.getBids().add(operacionBidAsk);
                                LOGGER.info("add BID oid:"+operacionBidAsk.getOid()+ " new Size: "+orderBookFull.getBids().size());
                            }else if(d.getTypeBuySell()==1) {
                                orderBookFull.getAsks().add(operacionBidAsk);
                                LOGGER.info("add ASK oid:"+operacionBidAsk.getOid()+ " new Size: "+orderBookFull.getAsks().size());
                            }else
                                LOGGER.error("Something it's wrong, the operation does not correspond to a valid one.");
                        }
                    }
                    //actualiza número de secuencia.
                    orderBookFull.setSequence(diffOrder.getSequence());
                });
    }

    private List<String> getAllOidsFromOrderBookFull(CopyOnWriteArrayList<Op> lstOp){
        return lstOp
                .parallelStream()
                .map(Op::getOid)
                .collect(Collectors.toList());
    }

    public List<Op> getBestXOp(int x, String Type){
        CopyOnWriteArrayList<Op> lst = null;
        if(Type.equalsIgnoreCase("Bids")){
            this.orderBookFull.getBids()
                    .stream()
                    .filter(b -> b.getAmount()!=null && !b.getAmount().equalsIgnoreCase(StringUtils.EMPTY))
                    .sorted(Comparator.comparing(a -> new BigDecimalStringConverter().fromString(a.getPrice()))
                    ).collect(Collectors.toList());
        } else {

        }
        return lst;
    }


    public OrderBook getOrderBookFull() {
        return this.orderBookFull;
    }
}
