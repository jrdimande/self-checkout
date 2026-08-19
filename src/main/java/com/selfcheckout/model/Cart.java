package com.selfcheckout.model;

import java.util.ArrayList;

public class Cart {
    private ArrayList<SaleItem> items;
    private double total;

    public ArrayList<SaleItem> getItems() {
        return items;
    }

    public Cart(){

    }

    public void setItems(ArrayList<SaleItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
