package com.selfcheckout.model;

public class Product {
    private String uuid;
    private String name;
    private double price;

    public Product(String uuid, String name, double price){
        this.uuid = uuid;
        this.name = name;
        this.price = price;

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
