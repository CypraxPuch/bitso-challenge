package com.ledze.bitcoin.bitsochallenge.util;


import com.ledze.bitcoin.bitsochallenge.client.OrderBook;
import com.ledze.bitcoin.bitsochallenge.pojo.DiffOrder;
import com.ledze.bitcoin.bitsochallenge.pojo.DiffOrderPayload;
import org.apache.commons.lang3.StringUtils;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonUtil {

    public static DiffOrder json2DiffOrder(String json) {
        DiffOrder diffOrder = null;

        try (StringReader sr = new StringReader(json)) {
            JsonParser jsonParser = Json.createParser(sr);

            diffOrder = new DiffOrder();
            while (jsonParser.hasNext()) {
                if (jsonParser.next().equals(JsonParser.Event.KEY_NAME)) {
                    switch (jsonParser.getString()) {
                        case "type":
                            jsonParser.next();
                            diffOrder.setType( Optional.ofNullable(jsonParser.getString()).orElse(StringUtils.EMPTY) );
                            break;
                        case "book":
                            jsonParser.next();
                            diffOrder.setBook( Optional.ofNullable(jsonParser.getString()).orElse(StringUtils.EMPTY) );
                            break;
                        case "sequence":
                            jsonParser.next();
                            diffOrder.setSequence( jsonParser.getLong() );
                            break;
                        case "payload":
                            diffOrder.setPayload( getDiffOrdersPayload(jsonParser) );
                            break;
                        default:
                    }
                }
            }
        }

        return diffOrder;
    }

    private static List<DiffOrderPayload> getDiffOrdersPayload(JsonParser jp) {
        List<DiffOrderPayload> diffOrderPayloadList = null;
        DiffOrderPayload diffOrderPayload = null;
        while (jp.hasNext()) {
            JsonParser.Event event = jp.next();
            if (event.equals(JsonParser.Event.START_ARRAY)) {
                diffOrderPayloadList = new ArrayList<>();
            } else if (event.equals(JsonParser.Event.START_OBJECT)) {
                diffOrderPayload = new DiffOrderPayload();
            } else if (event.equals(JsonParser.Event.KEY_NAME)) {
                switch (jp.getString()) {
                    case "d":
                        jp.next();
                        diffOrderPayload.setUnixTimestamp(jp.getLong());
                        break;
                    case "r":
                        jp.next();
                        diffOrderPayload.setRate(jp.getString());
                        break;
                    case "t":
                        jp.next();
                        diffOrderPayload.setTypeBuySell(jp.getInt());
                        break;
                    case "a":
                        jp.next();
                        diffOrderPayload.setAmount( Optional.ofNullable(jp.getString()).orElse(StringUtils.EMPTY) );
                        break;
                    case "v":
                        jp.next();
                        diffOrderPayload.setValue( Optional.ofNullable(jp.getString()).orElse(StringUtils.EMPTY) );
                        break;
                    case "o":
                        jp.next();
                        diffOrderPayload.setOid( Optional.ofNullable(jp.getString()).orElse(StringUtils.EMPTY) );
                        break;
                    case "s":
                        jp.next();
                        diffOrderPayload.setStatus( Optional.ofNullable(jp.getString()).orElse(StringUtils.EMPTY) );
                        break;
                    default:
                        break;
                }
            } else if (event.equals(JsonParser.Event.END_OBJECT)) {
                diffOrderPayloadList.add(diffOrderPayload);
            } else if (event.equals(JsonParser.Event.END_ARRAY)) {
                break;
            }
        }
        return diffOrderPayloadList;
    }

    public static OrderBook json2OrderBook(String json){
        OrderBook orderBook = null;
        try (StringReader sr = new StringReader(json)) {
            JsonParser jsonParser = Json.createParser(sr);

            orderBook = new OrderBook();
            while (jsonParser.hasNext()) {

            }
        }

        return orderBook;
    }


}