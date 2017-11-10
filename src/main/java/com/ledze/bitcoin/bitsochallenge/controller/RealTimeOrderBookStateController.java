package com.ledze.bitcoin.bitsochallenge.controller;

import com.ledze.bitcoin.bitsochallenge.client.Op;
import com.ledze.bitcoin.bitsochallenge.client.OrderBook;
import com.ledze.bitcoin.bitsochallenge.client.OrderBookClient;
import com.ledze.bitcoin.bitsochallenge.configuration.StaticApplicationContext;
import com.ledze.bitcoin.bitsochallenge.jms.Producer;
import com.ledze.bitcoin.bitsochallenge.pojo.DiffOrder;
import com.ledze.bitcoin.bitsochallenge.util.JsonUtil;
import com.ledze.bitcoin.bitsochallenge.websocket.DiffOrdersEndpoint;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class RealTimeOrderBookStateController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeOrderBookStateController.class);

    @FXML
    private TextField bestBidsAsks;

    @FXML
    private Label messageLabel;

    @FXML
    private TextArea consoleLogscreen;

    private OrderBookClient orderBookClient = (OrderBookClient) StaticApplicationContext.getContext().getBean("orderBookClient");
    private OrderBook orderBookFull = null;
    //private String response = null;
    private ReadOnlyStringWrapper response = new ReadOnlyStringWrapper("");


    @FXML
    private void showBestBidsAndAsks() {
        LOGGER.info("action showBestBidsAndAsks");
        messageLabel.setText("");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final String SENT_MESSAGE = "{\"action\":\"subscribe\",\"book\":\"btc_mxn\",\"type\":\"diff-orders\"}";
        final String WSS_URL = "wss://ws.bitso.com";

        DiffOrdersEndpoint clientEndpoint = new DiffOrdersEndpoint();

        clientEndpoint.setSubscriptionMessage(SENT_MESSAGE);

        try {
            ClientManager client = ClientManager.createClient();
            client.connectToServer(
                    clientEndpoint,
                    URI.create(WSS_URL)
            );

            response.set(clientEndpoint.getResponse());
            LOGGER.info("response: " + response);
            LOGGER.info("DIRECTO");
            //consoleLogscreen.textProperty().bind( response );
            //consoleLogscreen.appendText(response + "\n");

        } catch (IOException | DeploymentException | TimeoutException e) {
            LOGGER.error("Exception", e);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }


    @Bean
    @JmsListener(destination = "difforders.queue")
    public void receiveQueue(String text) {
        //LOGGER.info("queue: "+text);
        DiffOrder diffOrder = JsonUtil.json2DiffOrder(text);

        if (orderBookFull == null) {
            LOGGER.info("Calling order book rest service");
            String orderBookJsonString = orderBookClient.getOrderBookList("btc_mxn", "false");
            orderBookFull = JsonUtil.jsonToOrderBook(orderBookJsonString);
            LOGGER.info("orderBookFull:\n" + orderBookFull);
        }

        //LOGGER.info("DO-seq: "+diffOrder.getSequence()+" OB-seq: "+orderBookFull.getSequence());
        if (diffOrder.getSequence() > 0 && diffOrder.getSequence() > orderBookFull.getSequence()) {
            applyDiffOrder2FullOrderBookStruct(diffOrder);
        }
    }

    private void applyDiffOrder2FullOrderBookStruct(DiffOrder diffOrder) {

        List<String> listOidBids = getAllOidsFromOrderBookFull(this.orderBookFull.getBids());
        List<String> listOidAsks = getAllOidsFromOrderBookFull(this.orderBookFull.getAsks());

        diffOrder.getPayload()
                .parallelStream()
                .forEach(d -> {
                    if (d.getAmount() == null || d.getAmount().equalsIgnoreCase(StringUtils.EMPTY)) {
                        if (listOidBids.contains(d.getOid())) {
                            for (int x = 0; x < orderBookFull.getBids().size(); x++) {
                                Op o = orderBookFull.getBids().get(x);
                                if (d.getOid().equalsIgnoreCase(o.getOid())) {
                                    int prevSize = orderBookFull.getBids().size();
                                    orderBookFull.getBids().remove(o);
                                    LOGGER.info("(BID) elimina. antes: " + prevSize + " curr size: " + orderBookFull.getBids().size() + " orden:" + o.getOid());
                                    consoleLogscreen.appendText("(BID) elimina. antes: " + prevSize + " curr size: " + orderBookFull.getBids().size() + " orden:" + o.getOid());
                                }
                            }
                        } else if (listOidAsks.contains(d.getOid())) {
                            for (int x = 0; x < orderBookFull.getAsks().size(); x++) {
                                Op o = orderBookFull.getAsks().get(x);
                                if (d.getOid().equalsIgnoreCase(o.getOid())) {
                                    int prevSize = orderBookFull.getAsks().size();
                                    orderBookFull.getAsks().remove(o);
                                    LOGGER.info("(ASK) elimina. antes: " + prevSize + " curr size: " + orderBookFull.getAsks().size() + " orden:" + o.getOid());
                                    consoleLogscreen.appendText("(ASK) elimina. antes: " + prevSize + " curr size: " + orderBookFull.getAsks().size() + " orden:" + o.getOid());
                                }
                            }
                        }
                    } else if (d.getStatus().equalsIgnoreCase("open")) {
                        if (listOidBids.contains(d.getOid())) {
                            for (Op o : orderBookFull.getBids()) {
                                if (o.getOid().equalsIgnoreCase(d.getOid())) {
                                    o.setAmount(d.getAmount());
                                    o.setPrice(d.getRate());
                                    LOGGER.info("BID updated with:" + diffOrder);
                                    consoleLogscreen.appendText("BID updated with:" + diffOrder);
                                    break;
                                }
                            }
                            //LOGGER.info("orderBook updated BIDS on oid: "+d.getOid()+"\n"+orderBookFull);
                        } else if (listOidAsks.contains(d.getOid())) {
                            for (Op o : orderBookFull.getAsks()) {
                                if (o.getOid().equalsIgnoreCase(d.getOid())) {
                                    o.setAmount(d.getAmount());
                                    o.setPrice(d.getRate());
                                    LOGGER.info("ASK updated with:" + diffOrder);
                                    consoleLogscreen.appendText("ASK updated with:" + diffOrder);
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

                            if (d.getTypeBuySell() == 0) {
                                orderBookFull.getBids().add(operacionBidAsk);
                                LOGGER.info("add BID oid:" + operacionBidAsk.getOid() + " new Size: " + orderBookFull.getBids().size());
                                consoleLogscreen.appendText("add BID oid:" + operacionBidAsk.getOid() + " new Size: " + orderBookFull.getBids().size());
                            } else if (d.getTypeBuySell() == 1) {
                                orderBookFull.getAsks().add(operacionBidAsk);
                                LOGGER.info("add ASK oid:" + operacionBidAsk.getOid() + " new Size: " + orderBookFull.getAsks().size());
                                consoleLogscreen.appendText("add ASK oid:" + operacionBidAsk.getOid() + " new Size: " + orderBookFull.getAsks().size());
                            } else
                                LOGGER.error("Something it's wrong, the operation does not correspond to a valid one.");
                        }
                    }
                    //actualiza número de secuencia.
                    orderBookFull.setSequence(diffOrder.getSequence());
                    //indica a la queue bestops que hay bids y asks para que obtenga los mejores que se le indiquen
                    ((Producer) StaticApplicationContext.getContext().getBean("producer")).sendToBestOps("updated");
                });
    }

    private List<String> getAllOidsFromOrderBookFull(CopyOnWriteArrayList<Op> lstOp) {
        return lstOp
                .parallelStream()
                .map(Op::getOid)
                .collect(Collectors.toList());
    }

}
