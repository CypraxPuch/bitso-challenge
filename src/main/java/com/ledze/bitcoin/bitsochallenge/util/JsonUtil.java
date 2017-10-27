package com.ledze.bitcoin.bitsochallenge.util;


import com.ledze.bitcoin.bitsochallenge.pojo.DiffOrder;
import com.ledze.bitcoin.bitsochallenge.pojo.DiffOrderPayload;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
                            diffOrder.setType(jsonParser.getString());
                            break;
                        case "book":
                            jsonParser.next();
                            diffOrder.setBook(jsonParser.getString());
                            break;
                        case "sequence":
                            jsonParser.next();
                            diffOrder.setSequence(jsonParser.getInt());
                            break;
                        case "payload":
                            jsonParser.next();
                            diffOrder.setPayload(getDiffOrdersPayload(jsonParser));
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
            if (jp.next().equals(JsonParser.Event.START_ARRAY)) {
                diffOrderPayloadList = new ArrayList<>();
                jp.next();
            } else if (jp.next().equals(JsonParser.Event.START_OBJECT)) {
                diffOrderPayload = new DiffOrderPayload();
                jp.next();
            } else if (jp.next().equals(JsonParser.Event.KEY_NAME)) {
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
                        diffOrderPayload.setAmount(jp.getString());
                        break;
                    case "v":
                        jp.next();
                        diffOrderPayload.setValue(jp.getString());
                        break;
                    case "o":
                        jp.next();
                        diffOrderPayload.setOid(jp.getString());
                        break;
                    default:
                        break;
                }
            } else if (jp.next().equals(JsonParser.Event.END_OBJECT)) {
                diffOrderPayloadList.add(diffOrderPayload);
                jp.next();
            } else if (jp.next().equals(JsonParser.Event.END_ARRAY)) {
                jp.next();
                break;
            }
        }
        return diffOrderPayloadList;
    }

}
;