package com.ledze.bitcoin.bitsochallenge.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("orderbook.client")
public class OrderBookClientProperties {

    private int readTimeout;
    private int connectTimeout;
    private String orderBookUrl;
    private String availableBooksUrl;
    private String tradesUrl;

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getOrderBookUrl() {
        return orderBookUrl;
    }

    public void setOrderBookUrl(String orderBookUrl) {
        this.orderBookUrl = orderBookUrl;
    }

    public String getAvailableBooksUrl() {
        return availableBooksUrl;
    }

    public void setAvailableBooksUrl(String availableBooksUrl) {
        this.availableBooksUrl = availableBooksUrl;
    }

    public String getTradesUrl() {
        return tradesUrl;
    }

    public void setTradesUrl(String tradesUrl) {
        this.tradesUrl = tradesUrl;
    }
}
