package com.ledze.bitcoin.bitsochallenge.client;

import java.util.Date;
import java.util.List;

public class OrderBook {
    private String success;
    private Date updatedAt;
    private List<Bid> bids;
    private List<Ask> asks;
    private long sequence;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public List<Ask> getAsks() {
        return asks;
    }

    public void setAsks(List<Ask> asks) {
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
                ", updatedAt=" + updatedAt +
                ", bids=" + bids +
                ", asks=" + asks +
                ", sequence=" + sequence +
                '}';
    }
}
