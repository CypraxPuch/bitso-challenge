package com.ledze.bitcoin.bitsochallenge.pojo;

import java.util.List;

public class DiffOrder {
    private String type;
    private String book;
    private long sequence;
    private List<DiffOrderPayload> payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public List<DiffOrderPayload> getPayload() {
        return payload;
    }

    public void setPayload(List<DiffOrderPayload> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "DiffOrder{" +
                "type='" + type + '\'' +
                ", book='" + book + '\'' +
                ", sequence=" + sequence +
                ", payload=" + payload +
                '}';
    }
}
