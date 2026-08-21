package com.selfcheckout.model;

public class Customer {
    private String uuid;
    private String name;
    private double balance;

    public Customer(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
        this.balance = 0;
    }

    public Customer(String uuid, String name, double balance){
        this.uuid = uuid;
        this.name = name;
        this.balance = balance;
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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
