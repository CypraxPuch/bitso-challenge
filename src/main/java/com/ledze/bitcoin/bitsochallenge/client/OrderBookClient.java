package com.ledze.bitcoin.bitsochallenge.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;

@Component
public class OrderBookClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookClient.class);
    private final RestOperations rest;

    private final String orderBookServiceUrl;
    private final String availableBooksUrl;

    public OrderBookClient(final RestTemplateBuilder builder, final OrderBookClientProperties clientProperties) {
        this.rest = builder.setReadTimeout(clientProperties.getReadTimeout())
                .setConnectTimeout(clientProperties.getConnectTimeout())
                .build();
        this.orderBookServiceUrl = clientProperties.getOrderBookUrl();
        this.availableBooksUrl = clientProperties.getAvailableBooksUrl();
    }

    private String getJsonFromCall(String url, Object... params) {
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        // Send the request as GET
        ResponseEntity<String> result =
                rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class, params);

        return result.getBody();
    }

    public String getOrderBookList(String book, String aggregate) {

        LOGGER.info("getting order book list");
        return this.getJsonFromCall(this.orderBookServiceUrl, book, aggregate);
    }

    public String getAvailableBooks() {
        LOGGER.info("getting available books");
        return this.getJsonFromCall(this.availableBooksUrl);
    }

    private String getValueFromParser(JsonParser jsonParser){
        jsonParser.next();
        jsonParser.next();
        jsonParser.getString();
        return jsonParser.getString();
    }

    public Book getAvailableBook(String major_minor) {
        InputStream inputStream = null;
        URLConnection connection = null;

        try {
            connection = new URL(this.availableBooksUrl).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            inputStream = connection.getInputStream();
        } catch (MalformedURLException e) {
            LOGGER.error("error en la url", e);
        } catch (IOException e) {
            LOGGER.error("error en el stream", e);
        }

        JsonParser jsonParser = Json.createParser(inputStream);
        Book book = new Book();
        String bookName = null;
        String bookValue = null;
        while (jsonParser.hasNext()) {
            if (jsonParser.next().equals(JsonParser.Event.KEY_NAME)) {
                bookName = jsonParser.getString();
                if(bookName.equalsIgnoreCase("book")) {
                    jsonParser.next();
                    bookValue = jsonParser.getString();
                    if (bookValue.equalsIgnoreCase(major_minor)) {
                        book.setBook(bookValue);
                        book.setMinimumPrice(this.getValueFromParser(jsonParser));
                        book.setMaximumPrice(this.getValueFromParser(jsonParser));
                        book.setMinimumAmount(this.getValueFromParser(jsonParser));
                        book.setMaximumAmount(this.getValueFromParser(jsonParser));
                        book.setMinimumValue(this.getValueFromParser(jsonParser));
                        book.setMaximumValue(this.getValueFromParser(jsonParser));
                        break;
                    }
                }
            }
        }

        return book;
    }
}
