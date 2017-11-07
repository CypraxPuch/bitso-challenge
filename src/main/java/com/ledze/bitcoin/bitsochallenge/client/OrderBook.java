package com.ledze.bitcoin.bitsochallenge.client;

import java.util.concurrent.CopyOnWriteArrayList;

public class OrderBook {
    private String success;
    private String updatedAt;
    private CopyOnWriteArrayList<Op> bids = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Op> asks = new CopyOnWriteArrayList<>();
    private long sequence;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CopyOnWriteArrayList<Op> getBids() {
        return bids;
    }

    public void setBids(CopyOnWriteArrayList<Op> bids) {
        this.bids = bids;
    }

    public CopyOnWriteArrayList<Op> getAsks() {
        return asks;
    }

    public void setAsks(CopyOnWriteArrayList<Op> asks) {
        this.asks = asks;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "OrderBook{" +
                "success='" + success + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", bids=" + bids +
                ", asks=" + asks +
                ", sequence=" + sequence +
                '}';
    }
}
