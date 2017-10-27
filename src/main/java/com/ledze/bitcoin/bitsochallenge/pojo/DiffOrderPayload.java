package com.ledze.bitcoin.bitsochallenge.pojo;

public class DiffOrderPayload {
    private long unixTimestamp;
    private String rate;
    private int typeBuySell;
    private String amount;
    private String value;
    private String oid;

    public long getUnixTimestamp() {
        return unixTimestamp;
    }

    public void setUnixTimestamp(long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public int getTypeBuySell() {
        return typeBuySell;
    }

    public void setTypeBuySell(int typeBuySell) {
        this.typeBuySell = typeBuySell;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    @Override
    public String toString() {
        return "DiffOrderPayload{" +
                "unixTimestamp=" + unixTimestamp +
                ", rate='" + rate + '\'' +
                ", typeBuySell=" + typeBuySell +
                ", amount='" + amount + '\'' +
                ", value='" + value + '\'' +
                ", oid='" + oid + '\'' +
                '}';
    }
}
