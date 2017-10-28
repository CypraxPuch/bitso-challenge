package com.ledze.bitcoin.bitsochallenge.client;

public class Op {
    private String book;
    private String price;
    private String amount;
    private String oid;

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    @Override
    public String toString() {
        return "Op{" +
                "book='" + book + '\'' +
                ", price='" + price + '\'' +
                ", amount='" + amount + '\'' +
                ", oid='" + oid + '\'' +
                '}';
    }
}
