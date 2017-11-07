package com.ledze.bitcoin.bitsochallenge.service;

import com.ledze.bitcoin.bitsochallenge.client.OrderBookClient;
import com.ledze.bitcoin.bitsochallenge.task.DiffOrdersTask;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DiffOrdersService extends Service<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrdersService.class);

    private StringProperty url = new SimpleStringProperty(this, "url");
    public final void setUrl(String value) { url.set(value); }
    public final String getUrl() { return url.get(); }
    public final StringProperty urlProperty() { return url; }
    private static final String SENT_MESSAGE = "{\"action\":\"subscribe\",\"book\":\"btc_mxn\",\"type\":\"diff-orders\"}";

    @Override
    protected Task<String> createTask() {
        LOGGER.info("Subscribing and getting diff-orders info");
        return this.getDiffOrdersTask();
    }

    @Bean
    @Lazy
    private Task<String> getDiffOrdersTask(){
        DiffOrdersTask task = new DiffOrdersTask();
        task.setSubscriptionMessage(SENT_MESSAGE);
        task.setServerEndpointUrl(getUrl());

        return task;
    }
}
