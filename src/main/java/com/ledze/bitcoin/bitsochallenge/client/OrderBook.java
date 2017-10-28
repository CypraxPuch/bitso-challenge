package com.ledze.bitcoin.bitsochallenge.client;

import java.util.Date;
import java.util.List;

public class OrderBook {
    private String success;
    private String updatedAt;
    private List<Op> bids;
    private List<Op> asks;
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

    public List<Op> getBids() {
        return bids;
    }

    public void setBids(List<Op> bids) {
        this.bids = bids;
    }

    public List<Op> getAsks() {
        return asks;
    }

    public void setAsks(List<Op> asks) {
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
