package com.selfcheckout.model;

import java.time.LocalDateTime;

public class Sale {
    private int id;
    private String customer_uuid;
    private LocalDateTime saleDate;
    private double total;

    public Sale(String costumer_id, double total){
        this.customer_uuid = costumer_id;
        this.saleDate = LocalDateTime.now();
        this.total = total;
    }

    public Sale(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCustomer_uuid() {
        return customer_uuid;
    }

    public void setCustomer_uuid(String customer_uuid) {
        this.customer_uuid = customer_uuid;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
