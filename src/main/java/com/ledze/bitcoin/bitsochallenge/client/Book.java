package com.ledze.bitcoin.bitsochallenge.client;

public class Book {

    private String book;
    private String minimumPrice;
    private String maximumPrice;
    private String minimumAmount;
    private String maximumAmount;
    private String minimumValue;
    private String maximumValue;

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(String minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public String getMaximumPrice() {
        return maximumPrice;
    }

    public void setMaximumPrice(String maximumPrice) {
        this.maximumPrice = maximumPrice;
    }

    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(String maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public String getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(String minimumValue) {
        this.minimumValue = minimumValue;
    }

    public String getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(String maximumValue) {
        this.maximumValue = maximumValue;
    }

    @Override
    public String toString() {
        return "Book{" +
                "book='" + book + '\'' +
                ", minimumPrice='" + minimumPrice + '\'' +
                ", maximumPrice='" + maximumPrice + '\'' +
                ", minimumAmount='" + minimumAmount + '\'' +
                ", maximumAmount='" + maximumAmount + '\'' +
                ", minimumValue='" + minimumValue + '\'' +
                ", maximumValue='" + maximumValue + '\'' +
                '}';
    }
}
